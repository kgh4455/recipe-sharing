package com.project.config;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class OCRUtil {
    
    // 이미지 파일을 OCR API에 보내고 결과를 받아오는 메서드
    public static JSONObject getResult(String imgpath, String imgname) {
        JSONObject obj = null;

        // OCR API URL 및 Secret Key 설정
        String apiURL = "https://4hwl0xn26t.apigw.ntruss.com/custom/v1/39980/d8178aae87b9a429761b10178a0b5fb866e22f7edb02cbab95017a0805abe36d/infer";
        String secretKey = "bHFvdktqTGtLa3FHdXNMWVBzY1pBVkhQa05EQU1mU2M=";
        String imageFile = imgpath + imgname; // 이미지 파일 경로 c:\\images\\card.jpg
     
        
        try {
            File file = new File(imageFile);
            if (!file.exists()) { // 파일이 존재하는지 확인
                System.out.println("파일이 존재하지 않습니다: " + imageFile);
                return null;
            }

            // API URL 연결 설정
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setReadTimeout(30000); // 30초 동안 응답 대기
            con.setRequestMethod("POST"); // HTTP POST 요청
            String boundary = "----" + UUID.randomUUID().toString().replaceAll("-", ""); // multipart 요청을 위한 boundary 생성
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setRequestProperty("X-OCR-SECRET", secretKey); // 인증 키 설정

            // JSON 요청 메시지 생성
            JSONObject json = new JSONObject();
            json.put("version", "V2");
            json.put("requestId", UUID.randomUUID().toString()); // 요청 ID (고유 값)
            json.put("timestamp", System.currentTimeMillis()); // 현재 타임스탬프

            // 이미지 정보 설정
            JSONObject image = new JSONObject();
            image.put("format", "jpg"); // 이미지 형식
            image.put("name", "demo"); // 이미지 이름
            JSONArray images = new JSONArray();
            images.put(image);
            json.put("images", images);

            String postParams = json.toString(); // JSON 문자열 변환

            System.out.println("API 요청 JSON: " + postParams); // 요청 JSON 출력

            con.connect(); // API 연결

            // 요청 데이터를 전송하기 위한 스트림 생성
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            writeMultiPart(wr, postParams, file, boundary); // 멀티파트 데이터 작성
            wr.close();

            // API 응답 코드 확인
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader br;
            if (responseCode == 200) { // 정상 응답
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else { // 오류 응답
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            // 응답 데이터를 읽어오기
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            System.out.println("API 응답: " + response.toString()); // 응답 확인

            // JSON 응답 파싱
            obj = new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("OCR 요청 중 오류 발생: " + e.getMessage());
        }

        return obj; // OCR 결과 반환
    }

    // 멀티파트 요청을 작성하는 메서드
    private static void writeMultiPart(OutputStream out, String jsonMessage, File file, String boundary) throws IOException {
        StringBuilder sb = new StringBuilder();

        // JSON 데이터 추가
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition:form-data; name=\"message\"\r\n\r\n");
        sb.append(jsonMessage);
        sb.append("\r\n");
        System.out.println("sb.append: " + sb.toString());
        out.write(sb.toString().getBytes("UTF-8"));
        out.flush();

        // 파일 데이터 추가
        if (file != null && file.isFile()) {
            out.write(("--" + boundary + "\r\n").getBytes("UTF-8"));
            StringBuilder fileString = new StringBuilder();
            fileString.append("Content-Disposition:form-data; name=\"file\"; filename=\"");
            fileString.append(file.getName()).append("\"\r\n");
            fileString.append("Content-Type: application/octet-stream\r\n\r\n");
            out.write(fileString.toString().getBytes("UTF-8"));
            out.flush();

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
                out.write("\r\n".getBytes());
            }

            out.write(("--" + boundary + "--\r\n").getBytes("UTF-8"));
        }
        out.flush();
    }

    // OCR 응답 데이터를 Map 형태로 변환하는 메서드
    public static Map<String, String> getData(JSONObject obj) {
        Map<String, String> map = new HashMap<>();

        if (obj == null) { // 응답이 null인 경우
            System.out.println("OCR 응답이 null입니다.");
            return map;
        }

        // images 배열 확인
        JSONArray images = obj.getJSONArray("images");
        if (images == null || images.length() == 0) {
            System.out.println("OCR 응답에 images 키가 없거나 비어 있습니다.");
            return map;
        }

        // 첫 번째 이미지 정보 가져오기
        JSONObject jo1 = images.getJSONObject(0);
        if (jo1 == null) {
            System.out.println("images 배열의 첫 번째 객체가 null입니다.");
            return map;
        }

        // fields 배열 확인
        JSONArray fields = jo1.optJSONArray("fields");
        if (fields == null || fields.length() == 0) {
            System.out.println("OCR 응답에 fields 키가 없거나 비어 있습니다.");
            // fields가 비어 있거나 없으면 "title" 필드로 처리
            JSONObject title = jo1.optJSONObject("title");
            if (title != null) {
                String text = title.optString("inferText");
                if (text != null && !text.isEmpty()) {
                    map.put("titleText", text); // "호두" 텍스트 저장
                }
            }
            return map;
        }

        try {
            // OCR 결과에서 모든 정보 추출
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                String text = field.getString("inferText");

                // 추출된 텍스트를 Map에 저장
                map.put("field" + (i + 1), text);
            }
        } catch (Exception e) {
            System.out.println("OCR 데이터 파싱 중 오류 발생: " + e.getMessage());
        }

        return map; // 추출된 데이터 반환
    }
}
