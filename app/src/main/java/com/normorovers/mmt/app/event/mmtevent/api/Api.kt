package com.normorovers.mmt.app.event.mmtevent.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Api {
    fun retrofit(): Retrofit {
        val httpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor { chain ->
            val orriginal = chain.request()
            val requestBuilder = orriginal.newBuilder()
                    .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlJVVkVRVEZDUTBJelJVRXhNemxFUlRFd01ERkdPVVEwUkRVMVFUWXlOVVF5TXpsRE1EazNOQSJ9.eyJpc3MiOiJodHRwczovL25vcm1vcm92ZXJzLmF1LmF1dGgwLmNvbS8iLCJzdWIiOiJhdXRoMHw1YmZhODgzYTI5MmI0YjYyNTlmMzQyYjQiLCJhdWQiOlsiaHR0cHM6Ly9hZG1pbi5tbXQubm9ybW9yb3ZlcnMuY29tLyIsImh0dHBzOi8vbm9ybW9yb3ZlcnMuYXUuYXV0aDAuY29tL3VzZXJpbmZvIl0sImlhdCI6MTU2MTg2NjQyMiwiZXhwIjoxNTYxODczNjIyLCJhenAiOiJPUXM4Rm1wcWhLdXJ3Y2Y5WFIwNzZPMGlZQjVpaGRXSSIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUiLCJwZXJtaXNzaW9ucyI6WyJlbWFpbDpzZW5kIiwiZW5hYmxlZCIsInBheW1lbnRzOnZpZXciLCJ1c2VyczplZGl0Il19.FW5isRi-smZbklSsLAUD4h1Wu-I7qvpR-vSlmxEDED-JQc6NBXNENdpbbK0pEBAj7q4sf8krR__amTYEaDr9EwUGYKfmnWw0LZRNPJMNWA3uekKvKJgjvMllU6g1Dg-RZfLq4SM3kU_AeVmW_3nuNm7Qc73uiWEmluuW3p2YdS-xUNBpmwu0eeYMB3-uOAnNEKKnoGLJ8yrD5cmtRWyL6N3BZgKBf-CKEeYd8kOi9IvZE63413ers0GWzUmtP5WSXfZ2yFkgx4Bu8m5on2oWHR0w8N3XOdIO2o1mYcjIhuzSuVMX2SAQmTVRNPepA0_HxTNHWCdv38ZbWoX8sjtMBg")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        httpClientBuilder.connectTimeout(20, TimeUnit.SECONDS)
        httpClientBuilder.readTimeout(1, TimeUnit.MINUTES)

        val httpClient = httpClientBuilder.build()

        return Retrofit.Builder()
                .baseUrl("http://192.168.0.10:8082/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
    }
}