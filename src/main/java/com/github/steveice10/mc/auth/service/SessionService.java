package com.github.steveice10.mc.auth.service;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.profile.ProfileException;
import com.github.steveice10.mc.auth.exception.profile.ProfileLookupException;
import com.github.steveice10.mc.auth.exception.profile.ProfileNotFoundException;
import com.github.steveice10.mc.auth.exception.property.ProfileTextureException;
import com.github.steveice10.mc.auth.exception.property.PropertyException;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.util.Base64;
import com.github.steveice10.mc.auth.util.HTTP;
import com.github.steveice10.mc.auth.util.UUIDSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service used for session-related queries.
 */
public class SessionService {
    private static final String BASE_URL = "https://sessionserver.mojang.com/session/minecraft/";
    private static final String JOIN_URL = BASE_URL + "join";
    private static final String HAS_JOINED_URL = BASE_URL + "hasJoined";
    private static final String PROFILE_URL = BASE_URL + "profile";

    private static final String[] WHITELISTED_DOMAINS = { ".minecraft.net", ".mojang.com" };
    private static final PublicKey SIGNATURE_KEY;
    private static final Gson GSON;

    static {
        try(InputStream in = SessionService.class.getResourceAsStream("/yggdrasil_session_pubkey.der")) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int length = -1;
            while((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

            out.close();

            SIGNATURE_KEY = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(out.toByteArray()));
        } catch(Exception e) {
            throw new ExceptionInInitializerError("Missing/invalid yggdrasil public key.");
        }

        GSON = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDSerializer()).create();
    }

    private static boolean isWhitelistedDomain(String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL \"" + url + "\".");
        }

        String domain = uri.getHost();
        for(String whitelistedDomain : WHITELISTED_DOMAINS) {
            if(domain.endsWith(whitelistedDomain)) {
                return true;
            }
        }

        return false;
    }

    private Proxy proxy;

    /**
     * Creates a new SessionService instance.
     */
    public SessionService() {
        this(Proxy.NO_PROXY);
    }

    /**
     * Creates a new SessionService instance.
     *
     * @param proxy Proxy to use when making HTTP requests.
     */
    public SessionService(Proxy proxy) {
        if(proxy == null) {
            throw new IllegalArgumentException("Proxy cannot be null.");
        }

        this.proxy = proxy;
    }

    /**
     * Calculates the server ID from a base string, public key, and secret key.
     *
     * @param base      Base server ID to use.
     * @param publicKey Public key to use.
     * @param secretKey Secret key to use.
     * @return The calculated server ID.
     * @throws IllegalStateException If the server ID hash algorithm is unavailable.
     */
    public String getServerId(String base, PublicKey publicKey, SecretKey secretKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(base.getBytes(StandardCharsets.ISO_8859_1));
            digest.update(secretKey.getEncoded());
            digest.update(publicKey.getEncoded());
            return new BigInteger(digest.digest()).toString(16);
        } catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException("Server ID hash algorithm unavailable.", e);
        }
    }

    /**
     * Joins a server.
     *
     * @param profile             Profile to join the server with.
     * @param authenticationToken Authentication token to join the server with.
     * @param serverId            ID of the server to join.
     * @throws RequestException If an error occurs while making the request.
     */
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws RequestException {
        JoinServerRequest request = new JoinServerRequest(authenticationToken, profile.getId(), serverId);
        HTTP.makeRequest(this.proxy, JOIN_URL, request, null);
    }

    /**
     * Gets the profile of the given user if they are currently logged in to the given server.
     *
     * @param name     Name of the user to get the profile of.
     * @param serverId ID of the server to check if they're logged in to.
     * @return The profile of the given user, or null if they are not logged in to the given server.
     * @throws RequestException If an error occurs while making the request.
     */
    public GameProfile getProfileByServer(String name, String serverId) throws RequestException {
        HasJoinedResponse response = HTTP.makeRequest(this.proxy, HAS_JOINED_URL + "?username=" + name + "&serverId=" + serverId, null, HasJoinedResponse.class);
        if(response != null && response.id != null) {
            GameProfile result = new GameProfile(response.id, name);
            result.setProperties(response.properties);
            return result;
        } else {
            return null;
        }
    }

    /**
     * Fills in the properties of a profile.
     *
     * @param profile Profile to fill in the properties of.
     * @return The given profile, after filling in its properties.
     * @throws ProfileException If the property lookup fails.
     */
    public GameProfile fillProfileProperties(GameProfile profile) throws ProfileException {
        if(profile.getId() == null) {
            return profile;
        }

        try {
            MinecraftProfileResponse response = HTTP.makeRequest(this.proxy, PROFILE_URL + "/" + UUIDSerializer.fromUUID(profile.getId()) + "?unsigned=false", null, MinecraftProfileResponse.class);
            if(response == null) {
                throw new ProfileNotFoundException("Couldn't fetch profile properties for " + profile + " as the profile does not exist.");
            }

            profile.setProperties(response.properties);
            return profile;
        } catch(RequestException e) {
            throw new ProfileLookupException("Couldn't look up profile properties for " + profile + ".", e);
        }
    }

    /**
     * Fills in the textures of a profile.
     *
     * @param profile       Profile to fill in the textures of.
     * @param requireSecure Whether to require the textures to be securely signed.
     * @return The given profile, after filling in its textures.
     * @throws PropertyException If an error occurs while retrieving the profile's textures.
     */
    public GameProfile fillProfileTextures(GameProfile profile, boolean requireSecure) throws PropertyException {
        Map<GameProfile.TextureType, GameProfile.Texture> textureMap = null;

        GameProfile.Property textures = profile.getProperty("textures");
        if(textures != null) {
            if(requireSecure) {
                if(!textures.hasSignature()) {
                    throw new ProfileTextureException("Signature is missing from textures payload.");
                }

                if(!textures.isSignatureValid(SIGNATURE_KEY)) {
                    throw new ProfileTextureException("Textures payload has been tampered with. (signature invalid)");
                }
            }

            MinecraftTexturesPayload result;
            try {
                String json = new String(Base64.decode(textures.getValue().getBytes(StandardCharsets.UTF_8)));
                result = GSON.fromJson(json, MinecraftTexturesPayload.class);
            } catch(Exception e) {
                throw new ProfileTextureException("Could not decode texture payload.", e);
            }

            if(result != null && result.textures != null) {
                for(GameProfile.Texture texture : result.textures.values()) {
                    if (!isWhitelistedDomain(texture.getURL())) {
                        throw new ProfileTextureException("Textures payload has been tampered with. (non-whitelisted domain)");
                    }
                }

                textureMap = result.textures;
            }
        }

        profile.setTextures(textureMap);
        return profile;
    }

    @Override
    public String toString() {
        return "SessionService{}";
    }

    private static class JoinServerRequest {
        private String accessToken;
        private UUID selectedProfile;
        private String serverId;

        protected JoinServerRequest(String accessToken, UUID selectedProfile, String serverId) {
            this.accessToken = accessToken;
            this.selectedProfile = selectedProfile;
            this.serverId = serverId;
        }
    }

    private static class HasJoinedResponse {
        public UUID id;
        public List<GameProfile.Property> properties;
    }

    private static class MinecraftProfileResponse {
        public UUID id;
        public String name;
        public List<GameProfile.Property> properties;
    }

    private static class MinecraftTexturesPayload {
        public long timestamp;
        public UUID profileId;
        public String profileName;
        public boolean isPublic;
        public Map<GameProfile.TextureType, GameProfile.Texture> textures;
    }
}
