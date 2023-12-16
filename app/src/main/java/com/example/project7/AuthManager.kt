package com.example.project7

import com.google.firebase.auth.FirebaseAuth

/**
 * manages authentication using firebase authentication
 * provides methods to sign up, sign in, and sign out users
 */
class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * creates a new user account with email and password
     * @param email user's email
     * @param password user's password
     * @param onComplete callback to be executed after operation completion
     */
    fun signUp(email: String, password: String, onComplete: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    /**
     * signs in a user with email and password
     * @param email user's email
     * @param password user's password
     * @param onComplete callback to be executed after operation completion
     */
    fun signIn(email: String, password: String, onComplete: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    /**
     * signs out the current user
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * gets the current signed-in user
     * @return the current firebase user
     */
    fun getCurrentUser() = auth.currentUser
}