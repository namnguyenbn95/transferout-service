package vn.vnpay.commoninterface.filter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vnpay.ecdh.ECDH;
import com.vnpay.ecdh.entity.PreData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import vn.vnpay.commoninterface.common.AESService;
import vn.vnpay.commoninterface.common.Constants;
import vn.vnpay.commoninterface.dto.LoggingMetadataDTO;
import vn.vnpay.commoninterface.request.BaseClientRequest;
import vn.vnpay.commoninterface.response.BaseClientResponse;
import vn.vnpay.commoninterface.service.CommonService;
import vn.vnpay.commoninterface.service.JwtService;
import vn.vnpay.commoninterface.service.SmeEndpointService;
import vn.vnpay.commons.security.ib.IbSecurity;
import vn.vnpay.commons.security.ib.dto.ClientMessage;
import vn.vnpay.dbinterface.entity.MongoLogEntity;
import vn.vnpay.dbinterface.entity.SmeEndpoint;
import vn.vnpay.dbinterface.entity.SmeKey;
import vn.vnpay.dbinterface.repository.MongoLogRepository;
import vn.vnpay.dbinterface.repository.SmeKeyRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@WebFilter(filterName = "myServletFilter", urlPatterns = "/*")
public class MyServletFilter extends OncePerRequestFilter {

    @Value("${spring.application.name}")
    private String springAppName;

    @Value("${server.ip}")
    private String ipServer;

    @Autowired
    private SmeKeyRepository smeKeyRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private Gson gson;

    @Autowired
    private MongoLogRepository mongoLogRepository;

    @Autowired
    private SmeEndpointService smeEndpointService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtService jwtService;

    private static final String KEY_DEFAULT = "0000000001";
    private static final String SERVER_COMMON_KEY = "867EF6AE785D87A64CA451E185B76B14EEFCD25274F0B0B5784C9EFAE4A48014DDB716E2B73D01B9492DEF3C43AE054F4C8EBDF6287B1452C5EA3CE659B5E65AFF94BFAD2C12E07E33FAE4C41F6614CD3A6FC4C079306D48EF8EE40AB6B5A026A58D095D88E2B4897F3CFB364ACC8033A64779F5B104689E797FD7993E4B7966B8CB2229A90C0AEF4CD3BE0A7CC0BB6705744EB99D822F531F1DFEA115EC386F2955992D10A184904E2BB1E9ABFB3499F9FA821FEA76B47CC206311BE06E2948BEB9861E9E7F189BB1F9E0F113DE6E96145FB481169C3616C01C66EF661D5C6A7E05F318FB502B89677874E67D741B5EE392871B0C0A1303BCF4D60EE32D2D1BEA0FD8E1AF2D8C4D3A4B1CA2959088162AD8F97BB9CBAC6E1A0C1DC2A3C810C4F32BB8447A7EE96A18761A04273222D1AAF6F0E88AD4D90B1FC596432B9F132ED00CC63FD9259F5C98A287485D0EAF7A48A105671C6ED9BF38E568795CFD56C8A52CF13D7B8F4ACFD92433EEC9561F5307CADE5714CFB3938000A880963319E2BD2D18286AF7674AF86C3E33754FE0723EE8B05FDA4CCBFA515963934C6C13E32715A2AAED1EA745A7615B425BB5B345F583736C3CEA2DF439E0179EEE3AF2447138329ECCB97D0AEC417F55C62E9DAC7149977C427DAF19D43E076C701440A6128C80EBEC011028B13D0AFA6D782ACBA606D2B65DD407720F9DAB8DD61BB79709B9B27401DD6CF1B7FE65FD08A4D99019B59FE646F59A41F431C75452F1E1DC04F19F4E9B9EC0FC6F53EA2C1464A9BD4F7A476025E4BBF961624EC7BB90DC816584AE5720989814260DFDB8498BB103C11BBC0FE8A1B3C0F655B73D301BB8A4BFDA491EC61DF944FFE0583171B5FDC12A9FAED3EF25780A5EF5829FC4EF9D503D8F094E37F186839DB8DB79A4A7C7F1BD7300E7D3FBEE78F080F637C0B41B472D2C7A9577D92BD1213258549C1CC55FC233D4F8C61F675E01A1B3671BEFDE2DD1519D89920E4F3693F651789E6824BC5CD2773ECC563AB9F475F63673E526B5EC7D449BE95E0B9A808289418B3654D06CD041B7B9A192F343BEFC4D66EEC36292CC93FB18AABFFB4ED022C5097DF74A8B1F56CAFCEFBD4B8678BBE4FA89F14D1F7849064D6E18E0BA73F3038D8E5E0E28C603DD689E7D5827FB9738BD275D09350B2D8FD64DBFF5BCEAB43DFB2672986C642178D685A4EB01A3FCA95A11C4F9E1F50FAFEDC3AC9C4B19BEBA9E0A107ADBD4B96B8088CF52F0A08B9F5340BBAD78A658F4886084515F4D72CB1E87BC68EAC18C03860922130BB96C3486A2F2A7347EA3CB09CFB27EF7AC27FFE6FEE58D9FACB4F0ED7CB2E31E6BAC21E39871A96A248653C1E98F279AE69F02366B96656C03BB3D43D93D5C847C9BAABEDFDEB675B014C733D623D7457510E2E9E34446D8CA1EC3922853CADE04878AE4D89F85391EADD10AAE1409FE052E9EC3A3766211A8CEBC05859F689C25F38247613486C6218EEC8FD819D898";
    private static final String JSON_REQ = "JSON_REQ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        long startTime = System.currentTimeMillis();
        MyHttpServletRequestWrapper requestWrapper = new MyHttpServletRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        requestWrapper.setCharacterEncoding("UTF-8");
        responseWrapper.setCharacterEncoding("UTF-8");
        log.info("HTTP method: {}", requestWrapper.getMethod());
        try {
            if (requestWrapper.getMethod().equalsIgnoreCase("POST")) {
                String requestUri = requestWrapper.getRequestURI();
                log.info("endpoint: {}", requestUri);
                LocalDateTime receivedTime = LocalDateTime.now();
                // Chỉ dùng profile trong quá trình dev để bypass việc mã hóa / giải mã bản tin
                String profile = requestWrapper.getHeader("X-Spring-Profile");
                String clientIp = requestWrapper.getHeader("X-Forwarded-For");
                log.info("X-Spring-Profile: {}", profile);
                log.info("X-Forwarded-For: {}", clientIp);
                LoggingMetadataDTO loggingMetadataDTO = LoggingMetadataDTO
                        .builder()
                        .clientIp(clientIp).build();
                if (!"/auth-service/v1/bank-hub/login".equalsIgnoreCase(requestUri) &&
                        (springAppName.equalsIgnoreCase("api-service") ||
                                requestUri.startsWith("/" + springAppName + "/v1/internal") ||
                                requestUri.contains("bank-hub"))) {
                    log.info("Internal request");
                    String jsonData = IOUtils.toString(requestWrapper.getReader());
                    if (!requestUri.contains("/sms/send")) {
                        log.info("Json Request: {}", jsonData);
                    }

                    JsonObject reqJson = gson.fromJson(jsonData, JsonObject.class);

                    // nếu là request từ bank-hub -> kiểm tra access token
                    if (requestUri.contains("bank-hub")) {
                        String token = requestWrapper.getHeader("Authorization");
                        log.info("access token {}", token);
                        if (Strings.isNullOrEmpty(token)) {
                            log.error("token is empty");

                            throw new Exception("Request unauthorized");
                        }
                        // verify token
                        if (!jwtService.verifyToken(token)) {
                            log.error("invalid token");

                            throw new Exception("Invalid access token");
                        }
                        JsonObject payload = jwtService.getPayLoad(token);
                        reqJson.addProperty("sessionId", payload.get("sid").getAsString());
                        jsonData = gson.toJson(reqJson);
                    }

                    requestWrapper.setAttribute(JSON_REQ, jsonData);
                    requestWrapper.resetInputStream(jsonData.getBytes(StandardCharsets.UTF_8));
                    filterChain.doFilter(requestWrapper, responseWrapper);
                    String resStr = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                    if (!Constants.ExcelExportEndpoint.exportEndpoints.contains(requestUri)) {
                        log.info("Json Response: {}", resStr);
                    } else {
                        log.info("Json Response: {}", "File Export");
                    }

                    responseWrapper.copyBodyToResponse();
                } else { // Client's request
                    String rawData = IOUtils.toString(requestWrapper.getReader());
                    log.info("Raw data from client: {}", rawData);

                    if (StringUtils.isBlank(rawData)) {
                        log.error("Data from client is blank.");
                        throw new ServletException();
                    }

                    String reqSource = requestWrapper.getHeader("X-Spring-Source");
                    log.info("X-Spring-Source: {}", reqSource);
                    String jsonData,
                            clientPublicKey = StringUtils.EMPTY,
                            serverPrivateKey = StringUtils.EMPTY,
                            keyId = StringUtils.EMPTY;
                    String clientPubKeyIB = StringUtils.EMPTY;
                    if (StringUtils.isNotBlank(reqSource) && "IB".equalsIgnoreCase(reqSource)) { // Request from IB
                        // Giải mã bản tin
                        ClientMessage clientMessage = gson.fromJson(rawData, ClientMessage.class);
                        log.debug("ClientMessage key: {}", clientMessage.getKey());
                        log.debug("ClientMessage data: {}", clientMessage.getData());
                        jsonData = IbSecurity.decryptRequest(clientMessage, String.class);
                        log.info("Json Request: {}", jsonData);
                        JsonObject ibReqJo = gson.fromJson(jsonData, JsonObject.class);
                        if (ibReqJo.has("clientPubKey")) {
                            clientPubKeyIB = ibReqJo.get("clientPubKey").getAsString();
                        }

                        // Add request source
                        ibReqJo.addProperty("source", Constants.SOURCE_IB);
                        jsonData = gson.toJson(ibReqJo);

                        // Add user to MDC
                        if (ibReqJo.has("user")) {
                            MDC.put("username", ibReqJo.get("user").getAsString());
                        }
                    } else {
                        // Pre-Decryption request data
                        PreData preData = ECDH.prD(rawData);
                        keyId = preData.getK();

                        // Validate key id from client
                        if (StringUtils.isBlank(keyId)) {
                            log.info("Key id is blank.");
                            throw new ServletException();
                        }

                        if (KEY_DEFAULT.equals(keyId)) {
                            log.info("Use key default.");
                            String decryptedCk = ECDH.cmkDe(SERVER_COMMON_KEY);
                            log.info("decryptedCk: {}", decryptedCk);
                            if (StringUtils.isBlank(decryptedCk)) {
                                throw new ServletException();
                            }
                            JsonObject ckJsonObject = gson.fromJson(decryptedCk, JsonObject.class);
                            clientPublicKey = ckJsonObject.get("publicKey").getAsString();
                            serverPrivateKey = ckJsonObject.get("privateKey").getAsString();
                        } else {
                            Optional<SmeKey> mbKeyOpt = smeKeyRepository.findById(Long.parseLong(keyId));
                            if (!mbKeyOpt.isPresent()) {
                                log.info("Key from DB not found!");
                                throw new ServletException();
                            }
                            SmeKey keyDb = mbKeyOpt.get();
                            clientPublicKey = AESService.decrypt(keyDb.getClientPublicKey());
                            serverPrivateKey = AESService.decrypt(keyDb.getServerPrivateKey());
                        }
                        if (StringUtils.isAnyBlank(clientPublicKey, serverPrivateKey)) {
                            log.info("Key must not be blank!");
                            throw new ServletException();
                        }

                        jsonData =
                                ECDH.pR(
                                        preData.getE(),
                                        preData.getK(),
                                        preData.getT(),
                                        preData.getN(),
                                        preData.getS(),
                                        clientPublicKey,
                                        serverPrivateKey);
                        log.info("Json Request: {}", jsonData);
                        JsonObject mbReqJo = gson.fromJson(jsonData, JsonObject.class);

                        // Add request source
                        mbReqJo.addProperty("source", Constants.SOURCE_MB);
                        jsonData = gson.toJson(mbReqJo);

                        // Add user to MDC
                        if (mbReqJo.has("user")) {
                            MDC.put("username", mbReqJo.get("user").getAsString());
                        }
                    }

                    // Forward json request to controller
                    requestWrapper.setAttribute(JSON_REQ, jsonData);
                    requestWrapper.resetInputStream(jsonData.getBytes(StandardCharsets.UTF_8));
                    filterChain.doFilter(requestWrapper, responseWrapper);

                    // Mã hóa bản tin trước khi trả về cho client
                    if (!Constants.ExcelExportEndpoint.exportEndpoints.contains(requestUri)) {
                        String resStr = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                        log.info("Json Response: {}", resStr);

                        // Lưu his access
                        LocalDateTime sentTime = LocalDateTime.now();
                        insertLogMongo(jsonData, resStr, requestUri, receivedTime, sentTime, loggingMetadataDTO);

                        String encryptedResp;
                        if (StringUtils.isNotBlank(reqSource)
                                && "IB".equalsIgnoreCase(reqSource)) { // Request from IB
                            ClientMessage respMessage = IbSecurity.encryptResponse(resStr, clientPubKeyIB);
                            encryptedResp = gson.toJson(respMessage);
                        } else {
                            encryptedResp = ECDH.fR(resStr, keyId, clientPublicKey, serverPrivateKey);
                        }
                        responseWrapper.resetBuffer();
                        responseWrapper.getWriter().write(encryptedResp);
                    } else {
                        log.info("Json Response: {}", "File Export");
                        LocalDateTime sentTime = LocalDateTime.now();
                        byte[] resData = responseWrapper.getContentAsByteArray();
                        BaseClientResponse baseResp = new BaseClientResponse();
                        if (resData == null) {
                            baseResp.setCode(Constants.ResCode.ERROR_96);
                            baseResp.setMessage(Constants.MessageDefault.MESAGE_NOT_FOUND_VI);
                        } else {
                            baseResp.setCode(Constants.ResCode.INFO_00);
                            baseResp.setMessage("Thành công");
                        }
                        insertLogMongo(jsonData, gson.toJson(baseResp), requestUri, receivedTime, sentTime, loggingMetadataDTO);
                    }

                    responseWrapper.copyBodyToResponse();
                }
                log.info("endpoint: {}, process_time: {} ms", requestUri, System.currentTimeMillis() - startTime);
                MDC.remove("username");
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            log.info("Error: ", e);
            responseWrapper.setContentType("application/json");
            responseWrapper.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseWrapper.resetBuffer();
            responseWrapper
                    .getWriter()
                    .write(
                            commonService.makeClientResponseString(
                                    Constants.ResCode.ERROR_96, e.getMessage()));
            responseWrapper.copyBodyToResponse();
        }
    }

    private void insertLogMongo(
            String req,
            String resp,
            String requestUri,
            LocalDateTime receivedTime,
            LocalDateTime sentTime, LoggingMetadataDTO loggingMetadataDTO) {
        CompletableFuture.runAsync(
                () -> {
                    List<SmeEndpoint> listEndpoint = smeEndpointService.getAllValidEndpoints();
                    Optional<SmeEndpoint> endpoint =
                            listEndpoint.stream()
                                    .filter(e -> e.getEndpoint().equalsIgnoreCase(requestUri))
                                    .findAny();
                    BaseClientRequest _baseReq = gson.fromJson(req, BaseClientRequest.class);
                    MongoLogEntity logEntity = modelMapper.map(_baseReq, MongoLogEntity.class);
                    logEntity.setIpServer(ipServer);
                    logEntity.setEndpoint(requestUri);
                    if (endpoint.isPresent()) {
                        logEntity.setEndpointDesc(endpoint.get().getDesc());
                    }

                    JsonObject resObj = gson.fromJson(resp, JsonObject.class);
                    logEntity.setReceivedTime(receivedTime);
                    logEntity.setSentTime(sentTime);
                    if (resObj.has("code")) {
                        logEntity.setResCode(resObj.get("code").getAsString());
                    }
                    if (resObj.has("message")) {
                        logEntity.setResMsg(resObj.get("message").getAsString());
                    }
                    if (resObj.has("traceId")) {
                        logEntity.setTraceId(resObj.get("traceId").getAsString());
                    }

                    // adding metadata
                    if (loggingMetadataDTO.getClientIp() != null) {
                        String[] arrIp = loggingMetadataDTO.getClientIp().split(",");
                        logEntity.setClientIp(arrIp[0]);
                    }

                    mongoLogRepository.save(logEntity);
                });
    }
}
