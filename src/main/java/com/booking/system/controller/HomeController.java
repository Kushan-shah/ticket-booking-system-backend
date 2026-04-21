package com.booking.system.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>SeatLock | API System</title>\n" +
                "    <style>\n" +
                "        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;800&display=swap');\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "            font-family: 'Inter', sans-serif;\n" +
                "        }\n" +
                "        body {\n" +
                "            background-color: #0f172a;\n" +
                "            color: #cbd5e1;\n" +
                "            min-height: 100vh;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            overflow: hidden;\n" +
                "            position: relative;\n" +
                "        }\n" +
                "        .bg-circle-1 {\n" +
                "            position: absolute;\n" +
                "            top: -100px;\n" +
                "            left: -100px;\n" +
                "            width: 400px;\n" +
                "            height: 400px;\n" +
                "            background: radial-gradient(circle, rgba(99,102,241,0.2) 0%, rgba(0,0,0,0) 70%);\n" +
                "            border-radius: 50%;\n" +
                "            z-index: 0;\n" +
                "            animation: float 8s ease-in-out infinite alternate;\n" +
                "        }\n" +
                "        .bg-circle-2 {\n" +
                "            position: absolute;\n" +
                "            bottom: -150px;\n" +
                "            right: -150px;\n" +
                "            width: 500px;\n" +
                "            height: 500px;\n" +
                "            background: radial-gradient(circle, rgba(236,72,153,0.15) 0%, rgba(0,0,0,0) 70%);\n" +
                "            border-radius: 50%;\n" +
                "            z-index: 0;\n" +
                "            animation: float 10s ease-in-out infinite alternate-reverse;\n" +
                "        }\n" +
                "        @keyframes float {\n" +
                "            0% { transform: translate(0, 0); }\n" +
                "            100% { transform: translate(30px, 30px); }\n" +
                "        }\n" +
                "        .glass-card {\n" +
                "            background: rgba(30, 41, 59, 0.6);\n" +
                "            backdrop-filter: blur(16px);\n" +
                "            -webkit-backdrop-filter: blur(16px);\n" +
                "            border: 1px solid rgba(255, 255, 255, 0.1);\n" +
                "            border-radius: 24px;\n" +
                "            padding: 50px;\n" +
                "            width: 90%;\n" +
                "            max-width: 800px;\n" +
                "            z-index: 10;\n" +
                "            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .badge {\n" +
                "            display: inline-block;\n" +
                "            background: rgba(99, 102, 241, 0.15);\n" +
                "            color: #818cf8;\n" +
                "            padding: 6px 14px;\n" +
                "            border-radius: 50px;\n" +
                "            font-size: 13px;\n" +
                "            font-weight: 600;\n" +
                "            text-transform: uppercase;\n" +
                "            letter-spacing: 1px;\n" +
                "            margin-bottom: 20px;\n" +
                "            border: 1px solid rgba(99, 102, 241, 0.2);\n" +
                "        }\n" +
                "        h1 {\n" +
                "            font-size: 3rem;\n" +
                "            font-weight: 800;\n" +
                "            color: #f8fafc;\n" +
                "            margin-bottom: 15px;\n" +
                "            line-height: 1.2;\n" +
                "        }\n" +
                "        .subtitle {\n" +
                "            font-size: 1.1rem;\n" +
                "            color: #94a3b8;\n" +
                "            margin-bottom: 40px;\n" +
                "            max-width: 600px;\n" +
                "            margin-left: auto;\n" +
                "            margin-right: auto;\n" +
                "        }\n" +
                "        .features-grid {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));\n" +
                "            gap: 20px;\n" +
                "            margin-bottom: 40px;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "        .feature-item {\n" +
                "            background: rgba(15, 23, 42, 0.4);\n" +
                "            padding: 20px;\n" +
                "            border-radius: 16px;\n" +
                "            border: 1px solid rgba(255, 255, 255, 0.05);\n" +
                "            transition: transform 0.3s ease, background 0.3s ease;\n" +
                "        }\n" +
                "        .feature-item:hover {\n" +
                "            transform: translateY(-5px);\n" +
                "            background: rgba(30, 41, 59, 0.8);\n" +
                "            border-color: rgba(99, 102, 241, 0.4);\n" +
                "        }\n" +
                "        .feature-icon {\n" +
                "            font-size: 24px;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        .feature-title {\n" +
                "            color: #e2e8f0;\n" +
                "            font-weight: 600;\n" +
                "            margin-bottom: 5px;\n" +
                "            font-size: 15px;\n" +
                "        }\n" +
                "        .feature-desc {\n" +
                "            font-size: 13px;\n" +
                "            color: #64748b;\n" +
                "        }\n" +
                "        .cta-button {\n" +
                "            display: inline-block;\n" +
                "            background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);\n" +
                "            color: white;\n" +
                "            text-decoration: none;\n" +
                "            padding: 14px 32px;\n" +
                "            border-radius: 12px;\n" +
                "            font-weight: 600;\n" +
                "            font-size: 16px;\n" +
                "            transition: all 0.3s ease;\n" +
                "            box-shadow: 0 10px 25px -5px rgba(99, 102, 241, 0.4);\n" +
                "            border: none;\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "        .cta-button:hover {\n" +
                "            transform: translateY(-2px);\n" +
                "            box-shadow: 0 15px 30px -5px rgba(99, 102, 241, 0.6);\n" +
                "        }\n" +
                "        .watermark {\n" +
                "            margin-top: 30px;\n" +
                "            font-size: 12px;\n" +
                "            color: #475569;\n" +
                "        }\n" +
                "        @media (max-width: 600px) {\n" +
                "            h1 { font-size: 2.2rem; }\n" +
                "            .glass-card { padding: 30px 20px; }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"bg-circle-1\"></div>\n" +
                "    <div class=\"bg-circle-2\"></div>\n" +
                "    <div class=\"glass-card\">\n" +
                "        <div class=\"badge\">AWS Production Cluster</div>\n" +
                "        <h1>SeatLock Engine</h1>\n" +
                "        <p class=\"subtitle\">A high-concurrency ticket booking backend engineered to guarantee strict data integrity under severe race conditions.</p>\n" +
                "        <div class=\"features-grid\">\n" +
                "            <div class=\"feature-item\">\n" +
                "                <div class=\"feature-icon\">🔒</div>\n" +
                "                <div class=\"feature-title\">Optimistic Locking</div>\n" +
                "                <div class=\"feature-desc\">Prevents double booking via @Version tracking and fast HTTP 409 failures.</div>\n" +
                "            </div>\n" +
                "            <div class=\"feature-item\">\n" +
                "                <div class=\"feature-icon\">⚡</div>\n" +
                "                <div class=\"feature-title\">High Concurrency</div>\n" +
                "                <div class=\"feature-desc\">Idempotent endpoints and scalable JWT architecture strictly verified by k6.</div>\n" +
                "            </div>\n" +
                "            <div class=\"feature-item\">\n" +
                "                <div class=\"feature-icon\">🚀</div>\n" +
                "                <div class=\"feature-title\">N+1 Eliminated</div>\n" +
                "                <div class=\"feature-desc\">Optimized JPA EntityGraphs fetching complex data payloads in O(1) queries.</div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <a href=\"/swagger-ui/index.html\" class=\"cta-button\">View Interactive API Documentation</a>\n" +
                "        <div class=\"watermark\">Java 21 • Spring Boot 3 • PostgreSQL • AWS RDS</div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
