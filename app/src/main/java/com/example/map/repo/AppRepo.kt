package com.example.map.repo

import android.net.Uri
import android.util.Log
import com.example.map.SavedPlaceModel
//import com.example.map.SavedPlaceModel
//import com.example.map.UserModel
import com.example.map.constant.AppConstant
import com.example.map.models.GooglePlaceModel
import com.example.map.network.RetrofitClient
import com.example.map.utility.State
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class AppRepo {



    private suspend fun uploadImage(uid: String, image: Uri): Uri {
        val firebaseStorage = Firebase.storage
        val storageReference = firebaseStorage.reference
        val task = storageReference.child(uid + AppConstant.PROFILE_PATH)
            .putFile(image).await()

        return task.storage.downloadUrl.await()

    }



    //!!!
    fun getPlaces(url: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))
        val response = RetrofitClient.retrofitApi.getNearByPlaces(url = url)

        Log.d("TAG", "getPlaces:  $response ")
        if (response.body()?.googlePlaceModelList?.size!! > 0) {
            Log.d(
                "TAG",
                "getPlaces:  Success called ${response.body()?.googlePlaceModelList?.size}"
            )

            emit(State.success(response.body()!!))
        } else {
            Log.d("TAG", "getPlaces:  failed called")
            emit(State.failed(response.body()!!.error!!))
        }


    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)



    fun addUserPlace(googlePlaceModel: GooglePlaceModel, userSavedLocaitonId: ArrayList<String>) =
        flow<State<Any>> {
            emit(State.loading(true))
            val auth = Firebase.auth
            val userDatabase =
                Firebase.database.getReference("Users").child(auth.uid!!).child("Saved Locations")
            val database =
                Firebase.database.getReference("Places").child(googlePlaceModel.placeId!!).get()
                    .await()
            if (!database.exists()) {
                val savedPlaceModel = SavedPlaceModel(
                    googlePlaceModel.name!!, googlePlaceModel.vicinity!!,
                    googlePlaceModel.placeId, googlePlaceModel.userRatingsTotal!!,
                    googlePlaceModel.rating!!, googlePlaceModel.geometry?.location?.lat!!,
                    googlePlaceModel.geometry.location.lng!!
                )

                addPlace(savedPlaceModel)
            }

            userSavedLocaitonId.add(googlePlaceModel.placeId)
            userDatabase.setValue(userSavedLocaitonId).await()
            emit(State.success(googlePlaceModel))


        }.flowOn(Dispatchers.IO).catch { emit(State.failed(it.message!!)) }

    private suspend fun addPlace(savedPlaceModel: SavedPlaceModel) {
        val database = Firebase.database.getReference("Places")
        database.child(savedPlaceModel.placeId).setValue(savedPlaceModel).await()
    }

    //!!!
    fun removePlace(userSavedLocationId: ArrayList<String>) = flow<State<Any>> {
        emit(State.loading(true))
        val auth = Firebase.auth
        val database =
            Firebase.database.getReference("Users").child(auth.uid!!).child("Saved Locations")

        database.setValue(userSavedLocationId).await()
        emit(State.success("Remove Successfully"))
    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)

    //
    fun getDirection(url: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        val response = RetrofitClient.retrofitApi.getDirection(url)

        if (response.body()?.directionRouteModels?.size!! > 0) {
            emit(State.success(response.body()!!))
        } else {
            emit(State.failed(response.body()?.error!!))
        }
    }.flowOn(Dispatchers.IO)
        .catch {
            if (it.message.isNullOrEmpty()) {
                emit(State.failed("No route found"))
            } else {
                emit(State.failed(it.message.toString()))
            }

        }

    //
    fun getUserLocations() = callbackFlow<State<Any>> {

        trySendBlocking(State.loading(true))

        val database: DatabaseReference?
        val placesList: ArrayList<SavedPlaceModel> = ArrayList()

        try {

            val auth = Firebase.auth
            val reference = Firebase.database.getReference("Places")
            database =
                Firebase.database.getReference("Users").child(auth.uid!!).child("Saved Locations")

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach { ds ->
                            reference.child(ds.getValue(String::class.java)!!).get()
                                .addOnSuccessListener {
                                    placesList.add(it.getValue(SavedPlaceModel::class.java)!!)
                                }


                        }

                        trySendBlocking(State.success(placesList))
                    } else {
                        Log.d("TAG", "onDataChange: no data found")
                        trySendBlocking(State.failed("No data found"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            }

            database.addValueEventListener(eventListener)

            awaitClose {
                Log.d("TAG", "getUserLocations: await close ")
                database.removeEventListener(eventListener)
            }


        } catch (e: Throwable) {
            e.printStackTrace()
            close(e)
        }
    }

//    fun updateName(name: String): Flow<State<Any>> = flow<State<Any>> {
//        emit(State.loading(true))
//
//        val auth = Firebase.auth
//        val database = Firebase.database.getReference("Users").child(auth.uid!!)
//        val profileChangeRequest = UserProfileChangeRequest.Builder()
//            .setDisplayName(name)
//            .build()
//
//        auth.currentUser?.updateProfile(profileChangeRequest)?.await()
//        val map: MutableMap<String, Any> = HashMap()
//
//        map["username"] = name
//        database.updateChildren(map)
//        emit(State.success("Username updated"))
//
//    }.flowOn(Dispatchers.IO)
//        .catch {
//            emit(State.failed(it.message!!.toString()))
//        }
//
//    fun updateImage(image: Uri): Flow<State<Any>> = flow<State<Any>> {
//        emit(State.loading(true))
//
//        val auth = Firebase.auth
//        val path = uploadImage(auth.uid!!, image).toString()
//        val database = Firebase.database.getReference("Users").child(auth.uid!!)
//        val profileChangeRequest = UserProfileChangeRequest.Builder()
//            .setPhotoUri(Uri.parse(path))
//            .build()
//
//        auth.currentUser?.updateProfile(profileChangeRequest)?.await()
//
//        val map: MutableMap<String, Any> = HashMap()
//
//        map["image"] = path
//        database.updateChildren(map)
//        emit(State.success("Image updated"))
//
//    }.flowOn(Dispatchers.IO)
//        .catch {
//            emit(State.failed(it.message!!.toString()))
//        }
//
//
//    fun confirmEmail(authCredential: AuthCredential): Flow<State<Any>> = flow<State<Any>> {
//        emit(State.loading(true))
//
//        val auth = Firebase.auth
//        auth.currentUser?.reauthenticate(authCredential)?.await()
//        emit(State.success("User authenticate"))
//    }.flowOn(Dispatchers.IO)
//        .catch {
//            emit(State.failed(it.message!!.toString()))
//        }
//
//    fun updateEmail(email: String): Flow<State<Any>> = flow<State<Any>> {
//        emit(State.loading(true))
//
//        val auth = Firebase.auth
//        val database = Firebase.database.getReference("Users").child(auth.uid!!)
//        auth.currentUser?.updateEmail(email)?.await()
//        val map: MutableMap<String, Any> = HashMap()
//        map["email"] = email
//        database.updateChildren(map).await()
//
//        emit(State.success("Email updated"))
//    }.flowOn(Dispatchers.IO)
//        .catch {
//            emit(State.failed(it.message!!.toString()))
//        }
//
//    fun updatePassword(password: String): Flow<State<Any>> = flow<State<Any>> {
//        emit(State.loading(true))
//
//        val auth = Firebase.auth
//        auth.currentUser?.updatePassword(password)?.await()
//
//
//        emit(State.success("Email updated"))
//    }.flowOn(Dispatchers.IO)
//        .catch {
//            emit(State.failed(it.message!!.toString()))
//        }



}