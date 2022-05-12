package org.example.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.example.dto.*;
import org.example.entity.UserEntity;
import org.example.exception.TokenGenerationException;
import org.example.exception.UsernameAlreadyRegisteredException;
import org.example.repository.UserRepository;
import org.example.security.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RSASSASigner signer;

    public UserService(final UserRepository repository, final PasswordEncoder passwordEncoder) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final byte[] encodedKeyBytes = Files.readAllBytes(Paths.get(
                Optional.ofNullable(System.getenv("SIGN_KEY")).orElse("C:\\Users\\Apolena\\Desktop\\IT Academy 2 уровень\\17 лекция\\tomcat-embed\\sign.key")
        ));
        final byte[] keyBytes = Base64.getMimeDecoder().decode(encodedKeyBytes);
        final PrivateKey signKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(
                keyBytes
        ));
        this.signer = new RSASSASigner(signKey);
    }

    public UserMeResponseDto getMe(final Authentication auth) {
        if (auth.isAnonymous()) {
            throw new ForbiddenException();
        }
        return new UserMeResponseDto(
                auth.getName(),
                auth.getRoles()
        );
    }

    public List<UserGetAllResponseDTO> getAll(final Authentication auth) {
        if (!auth.hasRole(Roles.USERS_VIEW_ALL)) {
            throw new ForbiddenException();
        }
        return repository.getAll()
                .stream()
                .map(o -> new UserGetAllResponseDTO(o.getId(), o.getLogin(), o.getRoles()))
                .collect(Collectors.toList())
                ;
    }

    public UserRegisterResponseDTO register(final Authentication auth, final UserRegisterRequestDTO requestData) {
        if (!auth.isAnonymous()) {
            throw new ForbiddenException();
        }
        final String hashedPassword = passwordEncoder.encode(requestData.getPassword());
        repository
                .findByLogin(requestData.getLogin())
                .ifPresent(o -> {
                    throw new UsernameAlreadyRegisteredException(o.getLogin());
                });
        final UserEntity saved = repository.save(new UserEntity(
                0L,
                requestData.getLogin(),
                hashedPassword,
                new String[]{}
        ));
        try {
            final String token = createToken(saved);
            return new UserRegisterResponseDTO(
                    saved.getId(),
                    saved.getLogin(),
                    token
            );
        } catch (JOSEException e) {
            throw new TokenGenerationException(e);
        }
    }

    public LoginAuthentication authenticateByLoginAndPassword(final String login,final String password) {
        final UserEntity entity = repository.findByLogin(login)
                .orElseThrow(NotFoundException::new);
        if (!passwordEncoder.matches(password, entity.getPassword())) {
            throw new CredentialsNotMatchesException();
        }
        return new LoginAuthentication(entity.getId(), login, entity.getRoles());
    }

    public X509Authentication authenticateByCommonName(final String commonName) {
        final UserEntity entity = repository.findByLogin(commonName)
                .orElseThrow(NotFoundException::new);
        return new X509Authentication(entity.getId(), commonName, entity.getRoles());
    }

    public UserLoginResponseDTO login(final UserLoginRequestDTO requestData) {
        final UserEntity entity = repository.findByLogin(requestData.getLogin())
                .orElseThrow(NotFoundException::new);
        if (!passwordEncoder.matches(requestData.getPassword(), entity.getPassword())) {
            throw new CredentialsNotMatchesException();
        }
        try {
            final String token = createToken(entity);
            return new UserLoginResponseDTO(
                    entity.getId(),
                    entity.getLogin(),
                    token
            );
        } catch (JOSEException e) {
            throw new TokenGenerationException(e);
        }
    }

    private String createToken(final UserEntity entity) throws JOSEException {
        final Instant now = Instant.now();
        final JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS512)
                .build();
        final JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("id", entity.getId())
                .subject(entity.getLogin())
                .expirationTime(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .claim("roles", entity.getRoles())
                .build();
        final SignedJWT jwt = new SignedJWT(header, claimsSet);
        jwt.sign(signer);
        return jwt.serialize();
    }

    public UserCreateResponseDTO create(final Authentication auth,final UserCreateRequestDTO requestData) {
        if (!auth.hasRole(Roles.USERS_EDIT_ALL)) {
            throw new ForbiddenException();
        }
        final String hashedPassword = passwordEncoder.encode(requestData.getPassword());
        repository
                .findByLogin(requestData.getLogin())
                .ifPresent(o -> {
                    throw new UsernameAlreadyRegisteredException(o.getLogin());
                });
        final UserEntity saved = repository.save(new UserEntity(
                0L,
                requestData.getLogin(),
                hashedPassword,
                requestData.getRoles()
        ));
        return new UserCreateResponseDTO(
                saved.getId(),
                saved.getLogin(),
                saved.getRoles()
        );
    }

    public UserChangeRolesResponseDTO changeRoles(final Authentication auth,final UserChangeRolesRequestDTO requestData) {
        if (!auth.hasRole(Roles.USERS_EDIT_ALL)) {
            throw new ForbiddenException();
        }
        final UserEntity entity = repository.setRolesByLogin(
                requestData.getLogin(),
                requestData.getRoles()
        );
        return new UserChangeRolesResponseDTO(
                entity.getId(),
                entity.getLogin(),
                entity.getRoles()
        );
    }
}
