package ru.example.hyundai.data

import org.json.JSONObject

class Profile(json: String): JSONObject(json) {
    val phone: String = this.optString("phone");
    val email: String = this.optString("email");
    val firstName: String = this.optString("first_name");
    val lastName: String = this.optString("last_name");
    val middleName: String = this.optString("middle_name");
    val bday: String = this.optString("bday");
    val number: String = this.optString("number");
    val code: String = this.optString("code");
    val published: String = this.optString("published");
    val datePassport: String = this.optString("date_passport");
    val registrationAddress: String = this.optString("registration_address");
    val isApprovedData: Int = this.optInt("is_approved_data");
}