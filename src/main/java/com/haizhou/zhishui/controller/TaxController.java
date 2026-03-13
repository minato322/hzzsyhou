package com.haizhou.zhishui.controller;

import com.haizhou.zhishui.ai.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/tax")
public class TaxController {
    @Autowired
    private DeepSeekService deepSeekService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeTax(@RequestBody Map<String, Object> request) {
        try {
            String transactionType = (String) request.get("transactionType");
            String amount = request.get("amount").toString();
            String currency = (String) request.get("currency");
            String transactionDate = (String) request.get("transactionDate");
            String productCategory = (String) request.get("productCategory");
            String description = (String) request.get("description");

            String message = String.format(
                "请分析以下退税数据并给出最优退税方案：\n" +
                "交易类型：%s\n" +
                "交易金额：%s %s\n" +
                "交易日期：%s\n" +
                "商品类别：%s\n" +
                "详细说明：%s",
                transactionType, amount, currency, transactionDate, productCategory, description
            );

            String response = deepSeekService.getChatResponse(message);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", new HashMap<String, Object>() {{
                put("analysis", response);
            }});

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to analyze tax data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}