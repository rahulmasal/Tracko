package com.tracko.app.data.remote.api

import com.tracko.app.data.remote.dto.AdminDashboardResponse
import com.tracko.app.data.remote.dto.ApiResponse
import com.tracko.app.data.remote.dto.EmployeeDashboardResponse
import com.tracko.app.data.remote.dto.ManagerDashboardResponse
import retrofit2.Response
import retrofit2.http.GET

interface DashboardApi {

    @GET("api/v1/dashboard/employee")
    suspend fun getEmployeeDashboard(): Response<ApiResponse<EmployeeDashboardResponse>>

    @GET("api/v1/dashboard/manager")
    suspend fun getManagerDashboard(): Response<ApiResponse<ManagerDashboardResponse>>

    @GET("api/v1/dashboard/admin")
    suspend fun getAdminDashboard(): Response<ApiResponse<AdminDashboardResponse>>
}
