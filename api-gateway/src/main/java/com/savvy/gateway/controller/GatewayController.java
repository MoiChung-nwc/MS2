package com.savvy.gateway.controller;

import com.savvy.common.dto.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/gateway")
public class GatewayController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/health")
    public ResponseEntity<BaseResponse<Map<String, Object>>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("gateway", "API Gateway");
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(BaseResponse.success(health, "Gateway is running"));
    }

//    @GetMapping("/services")
//    public ResponseEntity<BaseResponse<Map<String, List<ServiceInstance>>>> getServices() {
//        List<String> services = discoveryClient.getServices();
//        Map<String, List<ServiceInstance>> serviceMap = new HashMap<>();
//
//        for (String service : services) {
//            List<ServiceInstance> instances = discoveryClient.getInstances(service);
//            serviceMap.put(service, instances);
//        }
//
//        return ResponseEntity.ok(BaseResponse.success(serviceMap, "Registered services"));
//    }
}
