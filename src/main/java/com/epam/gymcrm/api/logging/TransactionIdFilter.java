package com.epam.gymcrm.api.logging;

import jakarta.servlet.*;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class TransactionIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            String transactionId = UUID.randomUUID().toString();
            req.setAttribute("transactionId", transactionId);
            MDC.put("transactionId", transactionId);
            chain.doFilter(req, res);
        } finally {
            MDC.remove("transactionId");
        }
    }
}
