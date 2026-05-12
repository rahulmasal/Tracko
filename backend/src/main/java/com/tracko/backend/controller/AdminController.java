package com.tracko.backend.controller;

import com.tracko.backend.dto.ApiResponse;
import com.tracko.backend.model.*;
import com.tracko.backend.repository.*;
import com.tracko.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final GeofenceRepository geofenceRepository;
    private final ShiftRepository shiftRepository;
    private final HolidayRepository holidayRepository;
    private final CustomerMasterRepository customerRepository;
    private final ConfigService configService;
    private final ScoreService scoreService;
    private final ReportExportService reportExportService;

    // User Management
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(pageable)));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user,
                                                         @RequestParam(required = false) Set<Long> roleIds) {
        return ResponseEntity.ok(ApiResponse.success("User created", userService.createUser(user, roleIds)));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id,
                                                         @RequestBody User user,
                                                         @RequestParam(required = false) Set<Long> roleIds) {
        return ResponseEntity.ok(ApiResponse.success("User updated", userService.updateUser(id, user, roleIds)));
    }

    @PostMapping("/users/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleUserActive(@PathVariable Long id) {
        userService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.success("User status toggled"));
    }

    @PostMapping("/users/{id}/toggle-locked")
    public ResponseEntity<ApiResponse<Void>> toggleUserLocked(@PathVariable Long id) {
        userService.toggleLocked(id);
        return ResponseEntity.ok(ApiResponse.success("User lock status toggled"));
    }

    @PostMapping("/users/{id}/roles")
    public ResponseEntity<ApiResponse<User>> assignRoles(@PathVariable Long id,
                                                          @RequestBody Set<Long> roleIds) {
        return ResponseEntity.ok(ApiResponse.success(userService.assignRoles(id, roleIds)));
    }

    // Role Management
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleRepository.findAll()));
    }

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(ApiResponse.success("Role created", roleRepository.save(role)));
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<Role>> updateRole(@PathVariable Long id, @RequestBody Role role) {
        Role existing = roleRepository.findById(id).orElseThrow();
        existing.setName(role.getName());
        existing.setDescription(role.getDescription());
        existing.setPermissions(role.getPermissions());
        return ResponseEntity.ok(ApiResponse.success("Role updated", roleRepository.save(existing)));
    }

    @PostMapping("/roles/{id}/permissions")
    public ResponseEntity<ApiResponse<Role>> assignRolePermissions(@PathVariable Long id,
                                                                    @RequestBody Set<Long> permissionIds) {
        Role role = roleRepository.findById(id).orElseThrow();
        role.setPermissions(Set.copyOf(permissionRepository.findAllById(permissionIds)));
        return ResponseEntity.ok(ApiResponse.success(roleRepository.save(role)));
    }

    // Config
    @GetMapping("/config")
    public ResponseEntity<ApiResponse<List<AppConfig>>> getAllConfig() {
        return ResponseEntity.ok(ApiResponse.success(configService.getAllConfig()));
    }

    @PutMapping("/config/{key}")
    public ResponseEntity<ApiResponse<AppConfig>> updateConfig(
            @PathVariable String key,
            @RequestBody Object value,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String module,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(configService.updateConfig(
            key, value, description, module, userDetails.getUserId())));
    }

    // Geofences
    @GetMapping("/geofences")
    public ResponseEntity<ApiResponse<List<Geofence>>> getAllGeofences() {
        return ResponseEntity.ok(ApiResponse.success(geofenceRepository.findByIsActiveTrue()));
    }

    @PostMapping("/geofences")
    public ResponseEntity<ApiResponse<Geofence>> createGeofence(@RequestBody Geofence geofence) {
        return ResponseEntity.ok(ApiResponse.success("Geofence created", geofenceRepository.save(geofence)));
    }

    @PutMapping("/geofences/{id}")
    public ResponseEntity<ApiResponse<Geofence>> updateGeofence(@PathVariable Long id,
                                                                 @RequestBody Geofence geofence) {
        Geofence existing = geofenceRepository.findById(id).orElseThrow();
        existing.setName(geofence.getName());
        existing.setDescription(geofence.getDescription());
        existing.setLat(geofence.getLat());
        existing.setLng(geofence.getLng());
        existing.setRadiusMeters(geofence.getRadiusMeters());
        existing.setType(geofence.getType());
        return ResponseEntity.ok(ApiResponse.success("Geofence updated", geofenceRepository.save(existing)));
    }

    @DeleteMapping("/geofences/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGeofence(@PathVariable Long id) {
        geofenceRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Geofence deleted"));
    }

    // Shifts
    @GetMapping("/shifts")
    public ResponseEntity<ApiResponse<List<Shift>>> getAllShifts() {
        return ResponseEntity.ok(ApiResponse.success(shiftRepository.findAll()));
    }

    @PostMapping("/shifts")
    public ResponseEntity<ApiResponse<Shift>> createShift(@RequestBody Shift shift) {
        return ResponseEntity.ok(ApiResponse.success("Shift created", shiftRepository.save(shift)));
    }

    @PutMapping("/shifts/{id}")
    public ResponseEntity<ApiResponse<Shift>> updateShift(@PathVariable Long id, @RequestBody Shift shift) {
        Shift existing = shiftRepository.findById(id).orElseThrow();
        existing.setName(shift.getName());
        existing.setStartTime(shift.getStartTime());
        existing.setEndTime(shift.getEndTime());
        existing.setGraceMinutes(shift.getGraceMinutes());
        existing.setLateThresholdMinutes(shift.getLateThresholdMinutes());
        return ResponseEntity.ok(ApiResponse.success("Shift updated", shiftRepository.save(existing)));
    }

    // Holidays
    @GetMapping("/holidays")
    public ResponseEntity<ApiResponse<List<Holiday>>> getAllHolidays() {
        return ResponseEntity.ok(ApiResponse.success(holidayRepository.findAll()));
    }

    @PostMapping("/holidays")
    public ResponseEntity<ApiResponse<Holiday>> createHoliday(@RequestBody Holiday holiday) {
        return ResponseEntity.ok(ApiResponse.success("Holiday created", holidayRepository.save(holiday)));
    }

    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHoliday(@PathVariable Long id) {
        holidayRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Holiday deleted"));
    }

    // Customers
    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<CustomerMaster>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.success(customerRepository.findByIsActiveTrue()));
    }

    @PostMapping("/customers")
    public ResponseEntity<ApiResponse<CustomerMaster>> createCustomer(@RequestBody CustomerMaster customer) {
        return ResponseEntity.ok(ApiResponse.success("Customer created", customerRepository.save(customer)));
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<CustomerMaster>> updateCustomer(@PathVariable Long id,
                                                                       @RequestBody CustomerMaster customer) {
        CustomerMaster existing = customerRepository.findById(id).orElseThrow();
        existing.setName(customer.getName());
        existing.setCompany(customer.getCompany());
        existing.setEmail(customer.getEmail());
        existing.setPhone(customer.getPhone());
        existing.setMobile(customer.getMobile());
        existing.setAddress(customer.getAddress());
        existing.setCity(customer.getCity());
        existing.setState(customer.getState());
        existing.setPincode(customer.getPincode());
        existing.setGstNumber(customer.getGstNumber());
        return ResponseEntity.ok(ApiResponse.success("Customer updated", customerRepository.save(existing)));
    }

    // Reports
    @GetMapping("/reports/attendance/excel")
    public ResponseEntity<byte[]> exportAttendanceExcel(
            @RequestParam(required = false) java.time.LocalDate startDate,
            @RequestParam(required = false) java.time.LocalDate endDate) {
        java.time.LocalDate start = startDate != null ? startDate : java.time.LocalDate.now().withDayOfMonth(1);
        java.time.LocalDate end = endDate != null ? endDate : java.time.LocalDate.now();
        byte[] report = reportExportService.generateExcelAttendanceReport(start, end, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("filename", "attendance-report.xlsx");
        return ResponseEntity.ok().headers(headers).body(report);
    }

    @GetMapping("/reports/attendance/pdf")
    public ResponseEntity<byte[]> exportAttendancePdf(
            @RequestParam(required = false) java.time.LocalDate startDate,
            @RequestParam(required = false) java.time.LocalDate endDate) {
        java.time.LocalDate start = startDate != null ? startDate : java.time.LocalDate.now().withDayOfMonth(1);
        java.time.LocalDate end = endDate != null ? endDate : java.time.LocalDate.now();
        byte[] report = reportExportService.generatePdfAttendanceReport(start, end, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "attendance-report.pdf");
        return ResponseEntity.ok().headers(headers).body(report);
    }

    @PostMapping("/scores/update-formula")
    public ResponseEntity<ApiResponse<Void>> updateScoreFormula(@RequestBody String formula) {
        return ResponseEntity.ok(ApiResponse.success("Score formula updated"));
    }
}
