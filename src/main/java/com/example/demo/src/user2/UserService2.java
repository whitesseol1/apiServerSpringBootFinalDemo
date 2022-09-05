package com.example.demo.src.user2;


import com.example.demo.config.BaseException;
import com.example.demo.src.user2.model.PostUserReq;
import com.example.demo.src.user2.model.PostUserRes;
import com.example.demo.src.user2.model.kakaoLoginRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class UserService2 {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao2 userDao2;
    private final UserProvider2 userProvider2;
    private final JwtService jwtService;


    @Autowired
    public UserService2(UserDao2 userDao2, UserProvider2 userProvider2, JwtService jwtService) {
        this.userDao2 = userDao2;
        this.userProvider2 = userProvider2;
        this.jwtService = jwtService;

    }

    //POST
    @Transactional
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {


        //회원아이디 중복
        if (userProvider2.checkId(postUserReq.getId()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_ID);
        }

        //이메일중복

        if (userProvider2.checkEmail(postUserReq.getEmail()) == 1) {

            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }



        String pwd;
        try {
            //암호화
            pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);

        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            if(postUserReq.getUserImg() == null){
                postUserReq.setUserImg("");
            }
            int userIdx = userDao2.createUser(postUserReq);
            int result = userDao2.insertAddress(userIdx, postUserReq.getAddress());
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt, userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getkakaoAccessToken(String code) throws BaseException{
        String access_token = "";
        String refresh_token = "";
        String reqURL = "https://kauth.kakao.com/app/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=f5ed479443d2f76761183cddd4d486fa"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:8080/app/oauth/kakao"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuffer result = new StringBuffer();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            //JsonParser parser = new JsonParser();
            //JsonElement element = parser.parse(result);
            //access_Token = element.getAsJsonObject().get("access_token").getAsString();
            //refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            JSONParser jsonParse = new JSONParser();

            Object obj = jsonParse.parse(result.toString());
            JSONObject json = (JSONObject) obj;

            access_token = (String) json.get("access_token");
            refresh_token = (String) json.get("refresh_token");

            br.close();
            bw.close();
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

        return access_token;
    }

   public kakaoLoginRes kakaoLogin(String access_Token) throws BaseException{

       String reqURL = "https://kapi.kakao.com/v2/user/me";

       //access_token을 이용하여 사용자 정보 조회
       try {
           URL url = new URL(reqURL);
           HttpURLConnection conn = (HttpURLConnection) url.openConnection();

           conn.setRequestMethod("POST");
           conn.setDoOutput(true);
           conn.setRequestProperty("Authorization", "Bearer " + access_Token); //전송할 header 작성, access_token전송

           //결과 코드가 200이라면 성공
           int responseCode = conn.getResponseCode();
           System.out.println("responseCode : " + responseCode);

           //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
           BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           String line = "";
           StringBuffer result = new StringBuffer();

           while ((line = br.readLine()) != null) {
               result.append(line);
           }
           System.out.println("response body : " + result);

           //Gson 라이브러리로 JSON파싱
        /*   JsonParser parser = new JsonParser();
           JsonElement element =  parser.parse(result);

           int id = element.getAsJsonObject().get("id").getAsInt();
           boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
           String email = "";
           if(hasEmail){
               email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
           }*/

           JSONParser jsonParse = new JSONParser();
           Object obj = jsonParse.parse(result.toString());
           JSONObject json = (JSONObject) obj;
           //변환된 josn객체 안에서 다시 json객체로 얻어내야 하는 것이
           // 바로 "properties"라는 속성이다.
           JSONObject props = (JSONObject) json.get("properties");
           //System.out.println("props : "+ props);

           String nickName = (String) props.get("nickname");
           String profileImage = (String) props.get("profile_image");

           JSONObject kakaoAccount = (JSONObject) json.get("kakao_account");
           String email = (String) kakaoAccount.get("email");
           System.out.println("email : " + email);

           JSONObject profile = (JSONObject) kakaoAccount.get("profile");
           nickName = (String) profile.get("nickname");
           profileImage = (String) profile.get("profile_image_url");

           System.out.println("nickName : " + nickName);
           System.out.println("email : " + email);

           br.close();
           kakaoLoginRes res = null;
           res.setEmail(email);
           res.setNickName(nickName);
           res.setProfileImage(profileImage);
           return res;
       } catch (Exception exception) {
           throw new BaseException(DATABASE_ERROR);
       }




   }


}