package com.example.demo.src.user2;


import com.example.demo.config.BaseException;
import com.example.demo.src.user2.model.PostLoginRes;
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
            //String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String getkakaoAccessToken(String code) throws BaseException{
        String access_token = "";
        String refresh_token = "";
        //String reqURL = "https://kauth.kakao.com/app/oauth/token";
        String reqURL = "https://kauth.kakao.com/oauth/token";

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
            sb.append("&redirect_uri=http://localhost:8080/app/users/oauth/kakao"); // TODO 인가코드 받은 redirect_uri 입력
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
            System.out.println("result.toString() : " + result.toString());
            System.out.println("jsonParse.parse(result.toString() : " + jsonParse.parse(result.toString()));
            System.out.println("obj : " + obj);
            System.out.println("json : " + json);
            access_token = (String) json.get("access_token");
            refresh_token = (String) json.get("refresh_token");
            System.out.println("access_token : " + access_token);
            br.close();
            bw.close();
        } catch (Exception exception) {
            System.out.println("exception : " + exception);
            throw new BaseException(DATABASE_ERROR);
        }

        return access_token;
    }

   public kakaoLoginRes kakaoLogin(String access_Token) throws BaseException{

       String reqURL = "https://kapi.kakao.com/v2/user/me";
       System.out.println("access_Token : " + access_Token);
       String email = "";
       String nickName = "";
       String profileImage = "";

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

               //String nickName = (String)props.get("nickname");
               //String profileImage = (String)props.get("profile_image");

               JSONObject kakaoAccount = (JSONObject) json.get("kakao_account");
                   System.out.println("kakaoAccount : " + kakaoAccount);

           /*if(kakaoAccount.get("email").toString()!= null) {*/
                   email =  kakaoAccount.get("email").toString();
                   System.out.println("email : " + email);


              /* }*/
               if((JSONObject) kakaoAccount.get("profile")!=null) {
                   JSONObject profile = (JSONObject) kakaoAccount.get("profile");
                   if(profile.get("nickname").toString()!=null){
                       nickName = profile.get("nickname").toString();
                       System.out.println("nickName : " + nickName);

                   }
                   if(profile.get("profile_image_url").toString()!=null){
                       profileImage = profile.get("profile_image_url").toString();
                       System.out.println("profileImage : " + profileImage);

                   }
               }

               br.close();

       } catch (Exception exception) {
           System.out.println("exception2 : " + exception);
           throw new BaseException(DATABASE_ERROR);
       }

       return new kakaoLoginRes(nickName,profileImage,email);


   }

   public PostLoginRes kakaoLogin2(kakaoLoginRes res) throws BaseException{
        String jwt=null;
        int userIdx =0;
        try {
            if(res.getEmail()!=null) {
                int result = userProvider2.checkKakaoEmail(res.getEmail());
                System.out.println("res.getEmail : " + res.getEmail());
                if (result == 1) {
                    userIdx = userDao2.selectKakaoUser(res.getEmail()); //카카오 로그인 한 적 있을때(카카오이메일은중복없는것으로가정)
                    jwt = jwtService.createJwt(userIdx);
                    //return new PostLoginRes(userIdx, jwt);
                } else if (result == 0) {
                    userIdx = userDao2.insertKakaoUser(res);
                    //카카오 로그인 한 적 없으면 insert(기존 회원가입 정보 있더라도 카카오 정보 저장을 위한 새로운 userIdx를 insert)
                    jwt = jwtService.createJwt(userIdx);
                    //return new PostLoginRes(userIdx, jwt);
                }
            }

        }catch(BaseException exception){
            throw new BaseException(DATABASE_ERROR);
        }
       return new PostLoginRes(userIdx, jwt);
   }




}