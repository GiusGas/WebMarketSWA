package it.univaq.swa.webmarket.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import it.univaq.swa.webmarket.utility.FakeDb;
import jakarta.ws.rs.core.UriInfo;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class JWTHelpers {

    private static JWTHelpers instance = null;
    private SecretKey jwtKey = null;

    private JWTHelpers() {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("HmacSha256");
            jwtKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean authenticateUser(String username, String password) {
        return FakeDb.authUser(username, password);
    }

    public SecretKey getJwtKey() {
        return jwtKey;
    }

    public String validateToken(String token) {
        Jws<Claims> jwsc = Jwts.parser().verifyWith(getJwtKey()).build().parseSignedClaims(token);
        return jwsc.getPayload().getSubject();
    }

    public String issueToken(UriInfo context, String username) {
        String token = Jwts.builder()
                .subject(username)
                .issuer(context.getAbsolutePath().toString())
                .issuedAt(new Date())
                .expiration(Date.from(LocalDateTime.now().plusMinutes(15L).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getJwtKey())
                .compact();
        return token;
    }

    public void revokeToken(String token) {
        jwtKey = null;
    }

    public static JWTHelpers getInstance() {
        if (instance == null) {
            instance = new JWTHelpers();
        }
        return instance;
    }
}
