package com.example.gestorjuridico.network

import com.example.gestorjuridico.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LegalApiService {

    @GET("api/v1/dashboard")
    suspend fun getDashboardSummary(): Response<DashboardSummary>

    @GET("api/v1/processes")
    suspend fun getAllProcesses(): Response<List<LegalProcess>>

    @GET("api/v1/processes/{id}")
    suspend fun getProcessDetail(@Path("id") processId: String): Response<LegalProcess>

    @POST("api/v1/ai/generate-draft")
    suspend fun generateLegalDraft(@Body request: AiDraftRequest): Response<AiDraftResponse>
}