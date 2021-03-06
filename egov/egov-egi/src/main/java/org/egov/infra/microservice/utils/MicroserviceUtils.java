/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.microservice.utils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.egov.infra.utils.ApplicationConstant.CITIZEN_ROLE_NAME;
import static org.egov.infra.utils.DateUtils.toDefaultDateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.infra.admin.master.entity.CustomUserDetails;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.RoleService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.contract.ActionRequest;
import org.egov.infra.microservice.contract.ActionResponse;
import org.egov.infra.microservice.contract.CreateUserRequest;
import org.egov.infra.microservice.contract.Position;
import org.egov.infra.microservice.contract.PositionRequest;
import org.egov.infra.microservice.contract.PositionResponse;
import org.egov.infra.microservice.contract.RequestInfoWrapper;
import org.egov.infra.microservice.contract.Task;
import org.egov.infra.microservice.contract.TaskResponse;
import org.egov.infra.microservice.contract.UserDetailResponse;
import org.egov.infra.microservice.contract.UserRequest;
import org.egov.infra.microservice.contract.UserSearchRequest;
import org.egov.infra.microservice.contract.UserSearchResponse;
import org.egov.infra.microservice.models.Assignment;
import org.egov.infra.microservice.models.BankAccount;
import org.egov.infra.microservice.models.BankAccountServiceMapping;
import org.egov.infra.microservice.models.BankAccountServiceMappingReq;
import org.egov.infra.microservice.models.BankAccountServiceMappingResponse;
import org.egov.infra.microservice.models.BusinessCategory;
import org.egov.infra.microservice.models.BusinessCategoryResponse;
import org.egov.infra.microservice.models.BusinessDetails;
import org.egov.infra.microservice.models.BusinessDetailsResponse;
import org.egov.infra.microservice.models.BusinessService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.EmployeeInfoResponse;
import org.egov.infra.microservice.models.FinancialStatus;
import org.egov.infra.microservice.models.FinancialStatusResponse;
import org.egov.infra.microservice.models.GlCodeMaster;
import org.egov.infra.microservice.models.GlCodeMasterResponse;
import org.egov.infra.microservice.models.Instrument;
import org.egov.infra.microservice.models.InstrumentAccountCode;
import org.egov.infra.microservice.models.InstrumentAccountCodeResponse;
import org.egov.infra.microservice.models.InstrumentRequest;
import org.egov.infra.microservice.models.InstrumentResponse;
import org.egov.infra.microservice.models.MasterDetail;
import org.egov.infra.microservice.models.MdmsCriteria;
import org.egov.infra.microservice.models.MdmsCriteriaReq;
import org.egov.infra.microservice.models.MdmsResponse;
import org.egov.infra.microservice.models.ModuleDetail;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.models.ReceiptRequest;
import org.egov.infra.microservice.models.ReceiptResponse;
import org.egov.infra.microservice.models.Remittance;
import org.egov.infra.microservice.models.RemittanceRequest;
import org.egov.infra.microservice.models.RemittanceResponse;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.infra.microservice.models.ResponseInfo;
import org.egov.infra.microservice.models.TaxHeadMaster;
import org.egov.infra.microservice.models.TaxHeadMasterResponse;
import org.egov.infra.microservice.models.TaxPeriod;
import org.egov.infra.microservice.models.TaxPeriodResponse;
import org.egov.infra.microservice.models.UserInfo;
import org.egov.infra.persistence.entity.enums.UserType;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.web.support.ui.Inbox;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.jfree.util.Log;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@Service
public class MicroserviceUtils {

    private static final Logger LOGGER = Logger.getLogger(MicroserviceUtils.class);
    private static final String CLIENT_ID = "client.id";

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    protected EgovMasterDataCaching masterDataCache;

    @Autowired
    private Environment environment;

    @Autowired
    public RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private RoleService roleService;

    @Value("${egov.services.host}")
    private String hostUrl;

    @Value("${egov.services.workflow.url}")
    private String workflowServiceUrl;

    @Value("${egov.services.user.create.url}")
    private String userServiceUrl;

    @Value("${egov.services.user.approvers.url}")
    private String approverSrvcUrl;

    @Value("${egov.services.user.authsrvc.url}")
    private String authSrvcUrl;

    @Value("${egov.services.master.poistion.url}")
    private String positionSrvcUrl;

    @Value("${egov.services.master.actions.url}")
    private String actionSrvcUrl;

    @Value("${egov.services.user.search.url}")
    private String userSrcUrl;

    @Value("${egov.services.user.token.url}")
    private String tokenGenUrl;

    @Value("${egov.services.common.masters.businesscategory.url}")
    private String businessCategoryServiceUrl;

    @Value("${egov.services.common.masters.businessdetails.url}")
    private String businessDetailsServiceUrl;

    @Value("${egov.services.billing.service.taxheads.url}")
    private String taxheadsSearchUrl;

    @Value("${egov.services.billing.service.glcode.master.url}")
    private String glcodeMasterSearchUrl;

    @Value("${egov.services.egf.instrument.accountcode.search.url}")
    private String accountCodesSearchUrl;

    /*---- SI user details-----*/
    @Value("${si.microservice.user}")
    private String siUser;

    @Value("${si.microservice.password}")
    private String siPassword;

    @Value("${si.microservice.usertype}")
    private String siUserType;

    @Value("${si.microservice.scope}")
    private String siScope;

    @Value("${si.microservice.granttype}")
    private String siGrantType;

    @Value("${egov.services.billing.service.taxperiods.search}")
    private String taxperiodsSearchUrl;

    @Value("${egov.services.collection.service.receipts.search}")
    private String receiptSearchUrl;

    @Value("${egov.services.collection.service.basm.create}")
    private String bankAccountServiceMappingCreateUrl;

    @Value("${egov.services.collection.service.basm.search}")
    private String bankAccountServiceMappingSearchUrl;

    @Value("${egov.services.egf.master.financialstatuses.search}")
    private String financialStatusesSearchUrl;

    @Value("${egov.services.egf.instrument.search.url}")
    private String instrumentSearchUrl;

    @Value("${egov.services.egf.instrument.update.url}")
    private String instrumentUpdateUrl;

    @Value("${egov.services.collection.service.remittance.create}")
    private String remittanceCreateUrl;

    @Value("${egov.services.collection.service.receipt.update}")
    private String receiptUpdateUrl;

    @Value("${egov.services.master.mdms.search.url}")
    private String mdmsSearchUrl;
    
    @Value("${egov.services.egov-indexer.url}")
    private String egovIndexerUrl;
    
    @Value("${egov.finance.indexer.topic.name}")
    private String finIndexerTopic;
    
    private ObjectMapper mapper;
    
    public MicroserviceUtils() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public RequestInfo createRequestInfo() {
        final RequestInfo requestInfo = new RequestInfo();
        requestInfo.setApiId("apiId");
        requestInfo.setVer("ver");
        requestInfo.setTs(getEpochDate(new Date()));
        return requestInfo;
    }

    public RestTemplate createRestTemplate() {

        return restTemplate;
    }

    public UserInfo getUserInfo() {
        final User user = securityUtils.getCurrentUser();
        final List<org.egov.infra.microservice.models.RoleInfo> roles = new ArrayList<org.egov.infra.microservice.models.RoleInfo>();
        user.getRoles()
                .forEach(authority -> roles.add(new org.egov.infra.microservice.models.RoleInfo(authority.getName())));

        return new UserInfo(roles, user.getId(), user.getUuid(), user.getUsername(), user.getName(), user.getEmailId(),
                user.getMobileNumber(), user.getType().toString(), getTenentId());
    }

    public String getTenentId() {
        environment.getProperty(CLIENT_ID);
        String tenantId = ApplicationThreadLocals.getUserTenantId();
        // if (isNotBlank(clientId)) {
        // final StringBuilder stringBuilder = new StringBuilder();
        // stringBuilder.append(clientId).append('.').append(tenantId);
        // tenantId = stringBuilder.toString();
        // }
        return tenantId;
    }

    public String getUserToken() {
        String userToken = ApplicationThreadLocals.getUserToken();
        // if (adminToken == null)
        // adminToken = this.generateAdminToken(ApplicationThreadLocals.getUserTenantId());
        return userToken;
    }

    public void createUserMicroservice(final User user) {
        if (isNotBlank(userServiceUrl)) {

            if (user.getRoles().isEmpty() && user.getType().equals(UserType.CITIZEN))
                user.addRole(roleService.getRoleByName(CITIZEN_ROLE_NAME));

            final CreateUserRequest createUserRequest = new CreateUserRequest();
            final UserRequest userRequest = new UserRequest(user, getTenentId());
            createUserRequest.setUserRequest(userRequest);
            createUserRequest.setRequestInfo(createRequestInfo());

            final RestTemplate restTemplate = new RestTemplate();
            try {
                restTemplate.postForObject(userServiceUrl, createUserRequest, UserDetailResponse.class);
            } catch (final Exception e) {
                final String errMsg = "Exception while creating User in microservice ";
                // throw new ApplicationRuntimeException(errMsg, e);
                LOGGER.fatal(errMsg, e);
            }
        }
    }

    private Object getFinanceDeptCodes() {

        HashMap mdmsObj = this.getFinanceMdmsObj();
        if (mdmsObj != null)
            return mdmsObj.get("departments");
        return null;
    }

    private Object getFinanceDesginCodes() {

        HashMap mdmsObj = this.getFinanceMdmsObj();
        if (mdmsObj != null)
            return mdmsObj.get("designation");
        return null;
    }

    private HashMap getFinanceMdmsObj() {

        String mdmsUrl = this.hostUrl + this.mdmsSearchUrl;

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setAuthToken(getUserToken());
        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName("mapping");
        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setMasterDetails(Arrays.asList(masterDetail));
        moduleDetail.setModuleName("common-masters");
        MdmsCriteria mdmscriteria = new MdmsCriteria();
        mdmscriteria.setTenantId(getTenentId().split(Pattern.quote("."))[0]);
        mdmscriteria.setModuleDetails(Arrays.asList(moduleDetail));
        MdmsCriteriaReq mdmsrequest = new MdmsCriteriaReq();
        mdmsrequest.setRequestInfo(requestInfo);
        mdmsrequest.setMdmsCriteria(mdmscriteria);
        MdmsResponse response = restTemplate.postForObject(mdmsUrl, mdmsrequest, MdmsResponse.class);

        Map<String, JSONArray> mdmsmap = response.getMdmsRes().get("common-masters");
        if (null != mdmsmap && mdmsmap.size() > 0) {
            HashMap mdmObj = (HashMap) mdmsmap.get("mapping").get(0);
            return mdmObj;
        }

        return null;

    }
    
    public List<Department> getDepartments() {
        return getDepartments(null);
    }

    public List<Department> getDepartments(String code) {
        List<Department> deptList = new ArrayList<>();
        FilterRequest filterReq = new FilterRequest();
        try {
            if(!StringUtils.isEmpty(code) && code != null){
                filterReq.setCode(code);
            }else{
                final String deptCodes = (String) this.getFinanceDeptCodes();
                filterReq.setCodes(Arrays.asList(deptCodes.split(",")));
            }
            JSONArray mdmObj = getFinanceMdmsByModuleNameAndMasterDetails("common-masters", "Department", filterReq);
            mdmObj.stream().forEach(obj ->{
                LinkedHashMap<String, Object> lhm = (LinkedHashMap)obj;
                Department dept = new Department();
                dept.setCode(lhm.get("code").toString());
                dept.setName(lhm.get("name").toString());
                dept.setActive((Boolean)lhm.get("active"));
                deptList.add(dept);
            });
            return deptList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Department getDepartmentByCode(String departmentCode) {

        List<Department> deptlist = this.masterDataCache.get("egi-department");

        Department sDepartment = null;
        if (null != deptlist && !deptlist.isEmpty()) {

            List<org.egov.infra.microservice.models.Department> dept = deptlist.stream()
                    .filter(department -> departmentCode.equalsIgnoreCase(department.getCode()))
                    .collect(Collectors.toList());
            if (null != dept && dept.size() > 0)
                sDepartment = dept.get(0);
        }

        if (null == sDepartment) {
            sDepartment = this.fetchByDepartmentCode(departmentCode);

        }

        return sDepartment;

    }

    private Department fetchByDepartmentCode(String departmentCode) {
        List<Department> departments = getDepartments(departmentCode);
        try {
            return !departments.isEmpty() ? departments.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Designation> getDesignation(String code) {
            List<Designation> desgList = new ArrayList<>();
            FilterRequest filterReq =new FilterRequest();
            try {
                if(!StringUtils.isEmpty(code) && code != null){
                    filterReq.setCode(code);
                }else{
                    String desginCodes = (String) getFinanceDesginCodes();
                    filterReq.setCodes(Arrays.asList(desginCodes.split(",")));
                }
                JSONArray mdmObj =  getFinanceMdmsByModuleNameAndMasterDetails("common-masters", "Designation", filterReq);
                mdmObj.stream().forEach(obj ->{
                    LinkedHashMap<String, Object> lhm = (LinkedHashMap)obj;
                    Designation designation = new Designation();
                    designation.setCode(lhm.get("code").toString());
                    designation.setName(lhm.get("name").toString());
                    designation.setDescription(lhm.get("description").toString());
                    designation.setActive((Boolean)lhm.get("active"));
                    desgList.add(designation);
                });
                return desgList;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
}
    
    public JSONArray getFinanceMdmsByModuleNameAndMasterDetails(String moduleName,String name, FilterRequest filter){
        String mdmsUrl = this.hostUrl + this.mdmsSearchUrl;
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setAuthToken(getUserToken());
        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName(name);
        //Apply filter in the request
        if(null != filter){
            if(!StringUtils.isEmpty(filter.getCode()))
                masterDetail.setFilter("[?(@.code=='" + filter.getCode() + "')]");
            
            if(!StringUtils.isEmpty(filter.getName()))
                masterDetail.setFilter("[?(@.name=='" + filter.getName() + "')]");
            
            if(null != filter.getActive())
                masterDetail.setFilter("[?(@.active=='" + filter.getActive() + "')]");
            
            if(null != filter.getNames()) {
                List<String> names = filter.getNames().parallelStream()
                        .map(obj -> {
                            return "'"+obj+"'";
                        }).collect(Collectors.toList());
                masterDetail.setFilter("[?(@.name IN " + names + ")]");
            }
            
            if(null != filter.getCodes()) {
                List<String> codes = filter.getCodes().parallelStream()
                        .map(obj -> {
                            return "'"+obj+"'";
                        }).collect(Collectors.toList());
                masterDetail.setFilter("[?(@.code IN " + codes + ")]");
            }
        }
        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setMasterDetails(Arrays.asList(masterDetail));
        moduleDetail.setModuleName(moduleName);
        MdmsCriteria mdmscriteria = new MdmsCriteria();
        mdmscriteria.setTenantId(getTenentId().split(Pattern.quote("."))[0]);
        mdmscriteria.setModuleDetails(Arrays.asList(moduleDetail));
        MdmsCriteriaReq mdmsrequest = new MdmsCriteriaReq();
        mdmsrequest.setRequestInfo(requestInfo);
        mdmsrequest.setMdmsCriteria(mdmscriteria);
        try {
            MdmsResponse response = restTemplate.postForObject(mdmsUrl, mdmsrequest, MdmsResponse.class);
            Map<String, JSONArray> mdmsmap = response.getMdmsRes().get(moduleName);
            if (null != mdmsmap && mdmsmap.size() > 0) {
                return mdmsmap.get(name);
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }

    public List<Designation> getDesignations() {
        return getDesignation(null);
    }

    public List<EmployeeInfo> getApprovers(String departmentId, String designationId) {

        final RestTemplate restTemplate = createRestTemplate();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        final String approver_url = approverSrvcUrl + "?tenantId=" + getTenentId() + "&departments="
                + departmentId + "&designations="+designationId;
//                +"&asOnDate="+ getEpochDate();

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();
        // tenantId=default&assignment.departmentId=1&assignment.designationId=1&asOnDate=28/07/2018

        requestInfo.setAuthToken(getUserToken());
        requestInfo.setTs(getEpochDate(new Date()));
        
        reqWrapper.setRequestInfo(requestInfo);

        EmployeeInfoResponse empResponse = restTemplate.postForObject(approver_url, reqWrapper, EmployeeInfoResponse.class);
         return empResponse.getEmployees();
    }

    public EmployeeInfo getEmployeeByPositionId(Long positionId) {

        final RestTemplate restTemplate = createRestTemplate();

        final String employee_by_position_url = approverSrvcUrl + "?tenantId=" + getTenentId() + "&positionId="
                + positionId;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();
        requestInfo.setAuthToken(getUserToken());
        requestInfo.setTs(getEpochDate(new Date()));
        reqWrapper.setRequestInfo(requestInfo);
        LOGGER.info("call:" + employee_by_position_url);
        EmployeeInfoResponse empResponse = restTemplate.postForObject(employee_by_position_url, reqWrapper,
                EmployeeInfoResponse.class);
        if (empResponse.getEmployees() != null && !empResponse.getEmployees().isEmpty())
            return empResponse.getEmployees().get(0);
        else
            return null;
    }

    public CustomUserDetails getUserDetails(String user_token, String admin_token) {
        final RestTemplate restT = createRestTemplate();
        final String authurl = authSrvcUrl + "?access_token=" + user_token;

        RequestInfo reqInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        reqInfo.setAuthToken(admin_token);
        reqWrapper.setRequestInfo(reqInfo);
        LOGGER.info("call:" + authurl);
        CustomUserDetails user = restT.postForObject(authurl, reqWrapper, CustomUserDetails.class);
        // ResponseEntity<Object> response = restT.postForEntity(authurl,reqWrapper,Object.class);
        // this.processResponse(response.getBody());
        // CustomUserDetails user= null;
        return user;
    }

    public String generateAdminToken(String tenantId) {
        final RestTemplate restTemplate = createRestTemplate();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        header.add("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDplZ292LXVzZXItc2VjcmV0");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", this.siUser);
        map.add("scope", this.siScope);
        map.add("password", this.siPassword);
        map.add("grant_type", this.siGrantType);
        // TOD-DO - Mani : why this hard coding ?
        map.add("tenantId", tenantId);
        map.add("userType", this.siUserType);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, header);

        try {
            LOGGER.info("call:" + tokenGenUrl);
            Object response = restTemplate.postForObject(tokenGenUrl, request, Object.class);
            if (response != null)
                return String.valueOf(((HashMap) response).get("access_token"));
        } catch (RestClientException e) {
            LOGGER.info("Eror while getting admin authtoken", e);
            return null;
        }
        return null;
    }

    public UserSearchResponse getUserInfo(String auth_token, String tenantId, String userName) {
        final RestTemplate restT = createRestTemplate();

        RequestInfo req_header = new RequestInfo();
        UserSearchRequest request = new UserSearchRequest();

        req_header.setAuthToken(auth_token);
        request.setRequestInfo(req_header);
        request.setUserName(userName);
        request.setTenantId(tenantId);
        LOGGER.info("call:" + userSrcUrl);
        UserSearchResponse response = restT.postForObject(userSrcUrl, request, UserSearchResponse.class);
        return response;
    }

    public PositionResponse createPosition(String access_token, List<Position> positions) {

        final RestTemplate restT = createRestTemplate();

        PositionRequest posrequest = new PositionRequest();
        RequestInfo req_header = new RequestInfo();

        req_header.setAuthToken(access_token);
        posrequest.setRequestInfo(req_header);
        posrequest.setPosition(positions);
        LOGGER.info("call:" + positionSrvcUrl);
        PositionResponse response = restT.postForObject(positionSrvcUrl, posrequest, PositionResponse.class);

        return response;

    }

    public ActionResponse getActions(String authtoken, List<String> roles) {

        final RestTemplate restT = createRestTemplate();
        ActionRequest request = new ActionRequest();
        RequestInfo req_header = new RequestInfo();

        req_header.setAuthToken(authtoken);
        request.setRequestInfo(req_header);
        request.setTenantId(getTenentId());
        request.setRoleCodes(roles);
        request.setActionMaster("actions-test");
        request.setEnabled(true);
        LOGGER.info("call:" + actionSrvcUrl);
        ActionResponse response = restT.postForObject(actionSrvcUrl, request, ActionResponse.class);

        // response.getActions()
        return response;
    }

    public List<EmployeeInfo> getEmployee(Long empId, Date toDay, String departmentId, String designationId) {

        final RestTemplate restTemplate = createRestTemplate();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder empUrl = new StringBuilder(approverSrvcUrl);
        empUrl.append("?tenantId=" + getTenentId());

        if (empId != 0)
            empUrl.append("&ids=" + empId);
        if (toDay != null)
            empUrl.append("&asOnDate=" + getEpochDate(toDay));
        if (departmentId != null)
            empUrl.append("&departments=" + departmentId);
        if (designationId != null)
            empUrl.append("&designations=" + designationId);

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        requestInfo.setTs(getEpochDate(new Date()));
        reqWrapper.setRequestInfo(requestInfo);

        EmployeeInfoResponse empResponse = restTemplate.postForObject(empUrl.toString(), reqWrapper, EmployeeInfoResponse.class);
        return empResponse.getEmployees();
    }

    public EmployeeInfo getEmployeeById(Long empId) {

        final RestTemplate restTemplate = createRestTemplate();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder empUrl = new StringBuilder(approverSrvcUrl);
        empUrl.append("?tenantId=" + getTenentId());

        if (empId != 0)
            empUrl.append("&id=" + empId);

        empUrl.append("&asOnDate" + dateFormat.format(new Date()));

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        requestInfo.setTs(getEpochDate(new Date()));
        reqWrapper.setRequestInfo(requestInfo);
        EmployeeInfoResponse empResponse = restTemplate.postForObject(empUrl.toString(), reqWrapper, EmployeeInfoResponse.class);
        if (empResponse.getEmployees() != null && !empResponse.getEmployees().isEmpty())
            return empResponse.getEmployees().get(0);
        else
            return null;
    }

    public List<Assignment> getAssignments(String department, String designation) {
        List<Assignment> assignmentList = new ArrayList<>();
        List<EmployeeInfo> employeeInfos = getApprovers(department, designation);
        for (EmployeeInfo ei : employeeInfos) {
            for (Assignment a : ei.getAssignments()) {
                a.setEmployeeName(ei.getUser().getName());
            }
            assignmentList.addAll(ei.getAssignments());
        }
        return assignmentList;
    }

    public List<BusinessCategory> getBusinessCategories() {

        final RestTemplate restTemplate = createRestTemplate();

        final String bc_url = hostUrl + businessCategoryServiceUrl + "?tenantId=" + getTenentId();

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        
        reqWrapper.setRequestInfo(requestInfo);
        LOGGER.info("call:" + bc_url);
        BusinessCategoryResponse bcResponse = restTemplate.postForObject(bc_url, reqWrapper, BusinessCategoryResponse.class);
        return bcResponse.getBusinessCategoryInfo();
    }

    public List<BusinessDetails> getBusinessDetailsByCategoryCode(String categoryCode) {

        final RestTemplate restTemplate = createRestTemplate();

        final String bd_url = hostUrl + businessDetailsServiceUrl + "?tenantId=" + getTenentId()
                + "&businessType=MISCELLANEOUS&businessCategoryCode="
                + categoryCode;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        requestInfo.setTs(getEpochDate(new Date()));
        reqWrapper.setRequestInfo(requestInfo);

        BusinessDetailsResponse bcResponse = restTemplate.postForObject(bd_url, reqWrapper, BusinessDetailsResponse.class);
        return bcResponse.getBusinessDetails();
    }

    public List<BusinessDetails> getBusinessDetailsByType(String type) {

        final RestTemplate restTemplate = createRestTemplate();

        final String bd_url = hostUrl + businessDetailsServiceUrl + "?tenantId=" + getTenentId() + "&businessType=" + type;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        requestInfo.setTs(getEpochDate(new Date()));
        reqWrapper.setRequestInfo(requestInfo);

        BusinessDetailsResponse bcResponse = restTemplate.postForObject(bd_url, reqWrapper, BusinessDetailsResponse.class);
        if (bcResponse.getBusinessDetails() != null && !bcResponse.getBusinessDetails().isEmpty())
            return bcResponse.getBusinessDetails();
        else
            return null;
    }

    public List<BusinessDetails> getBusinessDetailsByCode(String code) {

        final RestTemplate restTemplate = createRestTemplate();

        final String bd_url = hostUrl + businessDetailsServiceUrl + "?tenantId=" + getTenentId() + "&businessDetailsCodes="
                + code;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        requestInfo.setTs(getEpochDate(new Date()));
        reqWrapper.setRequestInfo(requestInfo);

        BusinessDetailsResponse bcResponse = restTemplate.postForObject(bd_url, reqWrapper, BusinessDetailsResponse.class);
        if (bcResponse.getBusinessDetails() != null && !bcResponse.getBusinessDetails().isEmpty())
            return bcResponse.getBusinessDetails();
        else
            return null;
    }

    public BusinessDetails getBusinessDetailsById(Long id) {

        final RestTemplate restTemplate = createRestTemplate();

        final String bd_url = hostUrl + businessDetailsServiceUrl + "?tenantId=" + getTenentId() + "&id=" + id;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        requestInfo.setTs(getEpochDate(new Date()));
        reqWrapper.setRequestInfo(requestInfo);

        BusinessDetailsResponse bcResponse = restTemplate.postForObject(bd_url, reqWrapper, BusinessDetailsResponse.class);
        if (bcResponse.getBusinessDetails() != null && !bcResponse.getBusinessDetails().isEmpty())
            return bcResponse.getBusinessDetails().get(0);
        else
            return null;
    }

    public List<TaxHeadMaster> getTaxheadsByService(String service) {

        final RestTemplate restTemplate = createRestTemplate();

        final String url = hostUrl + taxheadsSearchUrl + "?tenantId=" + getTenentId() + "&service=" + service;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();
        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);
        TaxHeadMasterResponse response = restTemplate.postForObject(url, reqWrapper, TaxHeadMasterResponse.class);
        return response.getTaxHeadMasters();
    }

    public List<TaxHeadMaster> getTaxheads() {

        final RestTemplate restTemplate = createRestTemplate();

        final String url = hostUrl + taxheadsSearchUrl + "?tenantId=" + getTenentId();

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);

        TaxHeadMasterResponse response = restTemplate.postForObject(url, reqWrapper, TaxHeadMasterResponse.class);
        return response.getTaxHeadMasters();
    }

    public List<GlCodeMaster> getGlcodeMastersByService(String service) {

        final RestTemplate restTemplate = createRestTemplate();

        final String url = hostUrl + glcodeMasterSearchUrl + "?tenantId=" + getTenentId() + "&service=" + service;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);

        GlCodeMasterResponse response = restTemplate.postForObject(url, reqWrapper, GlCodeMasterResponse.class);
        return response.getGlCodeMasters();
    }

    public TaxPeriod getTaxPeriodsByService(String type) {

        final RestTemplate restTemplate = createRestTemplate();

        final String url = hostUrl + taxperiodsSearchUrl + "?tenantId=" + getTenentId() + "&service=" + type;
        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);

        TaxPeriodResponse response = restTemplate.postForObject(url, reqWrapper, TaxPeriodResponse.class);
        if (response != null && response.getTaxPeriods() != null && !response.getTaxPeriods().isEmpty())
            return response.getTaxPeriods().get(0);
        else
            return null;
    }

    public InstrumentAccountCode getInstrumentAccountCodeByType(String type) {

        final RestTemplate restTemplate = createRestTemplate();

        final String url = hostUrl + accountCodesSearchUrl + "?tenantId=" + getTenentId() + "&instrumentType.name=" + type;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);

        InstrumentAccountCodeResponse response = restTemplate.postForObject(url, reqWrapper, InstrumentAccountCodeResponse.class);
        if (response != null && response.getInstrumentAccountCodes() != null && !response.getInstrumentAccountCodes().isEmpty())
            return response.getInstrumentAccountCodes().get(0);
        else
            return null;
    }

    public List<BankAccountServiceMapping> getBankAcntServiceMappings() {

        final RestTemplate restTemplate = createRestTemplate();

        final String url = hostUrl + bankAccountServiceMappingSearchUrl + "?tenantId=" + getTenentId();

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);

        BankAccountServiceMappingResponse response = restTemplate.postForObject(url, reqWrapper,
                BankAccountServiceMappingResponse.class);
        if (response != null && response.getBankAccountServiceMapping() != null)
            return response.getBankAccountServiceMapping();
        else
            return null;
    }

    public List<BankAccountServiceMapping> getBankAcntServiceMappingsByBankAcc(String bankAccount, String businessDetails) {

        final RestTemplate restTemplate = createRestTemplate();

        String url = hostUrl + bankAccountServiceMappingSearchUrl + "?tenantId=" + getTenentId();

        if (bankAccount != null && !bankAccount.isEmpty() && !bankAccount.equalsIgnoreCase("-1")) {
            url = url + "&bankAccount="
                    + bankAccount;
        }
        if (businessDetails != null && !businessDetails.isEmpty() && !businessDetails.equalsIgnoreCase("-1")) {
            url = url + "&businessDetails="
                    + businessDetails;
        }

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);

        BankAccountServiceMappingResponse response = restTemplate.postForObject(url, reqWrapper,
                BankAccountServiceMappingResponse.class);
        if (response != null && response.getBankAccountServiceMapping() != null)
            return response.getBankAccountServiceMapping();
        else
            return null;
    }

    public List<BankAccountServiceMapping> createBankAcntServiceMappings(BankAccountServiceMapping basm) {

        final RestTemplate restTemplate = createRestTemplate();

        final String url = hostUrl + bankAccountServiceMappingCreateUrl;

        RequestInfo requestInfo = new RequestInfo();

        requestInfo.setAuthToken(getUserToken());

        BankAccountServiceMappingReq request = new BankAccountServiceMappingReq();
        request.setRequestInfo(requestInfo);
        request.setBankAccountServiceMapping(Collections.singletonList(basm));

        BankAccountServiceMappingResponse response = restTemplate.postForObject(url, request,
                BankAccountServiceMappingResponse.class);
        if (response != null && response.getBankAccountServiceMapping() != null)
            return response.getBankAccountServiceMapping();
        else
            return null;
    }

    public FinancialStatus getInstrumentStatusByCode(String code) {

        final String url = hostUrl + financialStatusesSearchUrl + "?tenantId="
                + getTenentId() + "&moduleType=Instrument&code=" + code;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);
        LOGGER.info("call:" + url);
        FinancialStatusResponse response = restTemplate.postForObject(url, reqWrapper, FinancialStatusResponse.class);
        if (response.getFinancialStatuses() != null && !response.getFinancialStatuses().isEmpty())
            return response.getFinancialStatuses().get(0);
        else
            return null;
    }

    public List<Instrument> getInstruments(String instrumentType, String transactionType, String instrumentStatus) {

        final String url = hostUrl + instrumentSearchUrl + "?tenantId=" + getTenentId() + "&instrumentTypes=" + instrumentType
                + "&transactionType=" + transactionType + "&financialStatuses=" + instrumentStatus;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);
        LOGGER.info("call:" + url);
        InstrumentResponse response = restTemplate.postForObject(url, reqWrapper, InstrumentResponse.class);

        return response.getInstruments();
    }

    public List<Instrument> getInstruments(String ids) {

        final String url = hostUrl + instrumentSearchUrl + "?tenantId=" + getTenentId() + "&ids=" + ids;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);
        LOGGER.info("call:" + url);
        InstrumentResponse response = restTemplate.postForObject(url, reqWrapper, InstrumentResponse.class);

        return response.getInstruments();
    }

    public List<Instrument> getInstrumentsByReceiptIds(String instrumentType, String instrumentStatus, String receiptIds) {

        final StringBuilder instrumentUrl = new StringBuilder(hostUrl + instrumentSearchUrl + "?tenantId=" + getTenentId());

        if (null != instrumentType)
            instrumentUrl.append("&instrumentTypes=" + instrumentType);
        if (null != instrumentStatus)
            instrumentUrl.append("&financialStatuses=" + instrumentStatus);

        if (null != receiptIds)
            instrumentUrl.append("&receiptIds=" + receiptIds);

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);
        LOGGER.info("call:" + instrumentUrl.toString());
        InstrumentResponse response = restTemplate.postForObject(instrumentUrl.toString(), reqWrapper, InstrumentResponse.class);

        return response.getInstruments();
    }

    public List<Receipt> getReceipts(String ids, String status, String serviceCodes, Date fromDate, Date toDate) {

        final String url = hostUrl + receiptSearchUrl + "?tenantId=" + getTenentId() + "&status=" + status
                + "&ids=" + ids + "&businessCodes=" + serviceCodes + "&fromDate=" + fromDate.getTime() + "&toDate="
                + toDate.getTime();

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);
        LOGGER.info("call:" + url);
        ReceiptResponse response = restTemplate.postForObject(url, reqWrapper, ReceiptResponse.class);

        return response.getReceipts();
    }

    public List<Receipt> getReceipts(String receiptNumbers) {

        final String url = hostUrl + receiptSearchUrl + "?tenantId=" + getTenentId() + "&receiptNumbers=" + receiptNumbers;

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);
        LOGGER.info("call:" + url);
        ReceiptResponse response = restTemplate.postForObject(url, reqWrapper, ReceiptResponse.class);

        return response.getReceipts();
    }

    public List<Receipt> getReceipts(String status, String serviceCode, String fund, String department, String receiptDate) {

        final String url = hostUrl + receiptSearchUrl + "?tenantId=" + getTenentId() + "&status=" + status
                + "&businessCodes=" + serviceCode + "&fund=" + fund + "&department=" + department + "&fromDate="
                + DateUtils.toDateUsingDefaultPattern(receiptDate).getTime() + "&toDate="
                + DateUtils.toDateUsingDefaultPattern(receiptDate).getTime();

        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();

        requestInfo.setAuthToken(getUserToken());
        reqWrapper.setRequestInfo(requestInfo);
        LOGGER.info("call:" + url);
        ReceiptResponse response = restTemplate.postForObject(url, reqWrapper, ReceiptResponse.class);

        return response.getReceipts();
    }

    public List<Receipt> searchReciepts(String classification, Date fromDate, Date toDate, String businessCode,
            String receiptNo) {

        return this.searchReciepts(classification, fromDate, toDate, businessCode, Arrays.asList(receiptNo));

    }

    public List<Receipt> searchReciepts(String classification, Date fromDate, Date toDate, String businessCode,
            List<String> receiptNos) {

        final StringBuilder url = new StringBuilder(hostUrl + receiptSearchUrl);
        final RequestInfoWrapper request = new RequestInfoWrapper();
        final RequestInfo requestinfo = new RequestInfo();

        String tenantId = getTenentId();
        requestinfo.setAuthToken(getUserToken());
        requestinfo.setUserInfo(getUserInfo());
        request.setRequestInfo(requestinfo);

        url.append("?tenantId=" + tenantId);

        if (null != classification)
            url.append("&classification=" + classification);

        if (null != fromDate)
            url.append("&fromDate=" + fromDate.getTime());

        if (null != toDate)
            url.append("&toDate=" + toDate.getTime());

        if (null != businessCode)
            url.append("&businessCode=" + businessCode);

        if (null != receiptNos && receiptNos.size() > 0) {
            url.append("&receiptNumbers=" + StringUtils.join(receiptNos, ","));
        }

        ReceiptResponse response = restTemplate.postForObject(url.toString(), request, ReceiptResponse.class);

        return response.getReceipts();
    }

    public InstrumentResponse reconcileInstruments(List<Instrument> instruments, String depositedBankAccountNum) {

        final StringBuilder url = new StringBuilder(hostUrl + instrumentUpdateUrl);
        FinancialStatus instrumentStatusReconciled = getInstrumentStatusByCode("Reconciled");
        for (Instrument i : instruments) {
            i.setFinancialStatus(instrumentStatusReconciled);
            if (depositedBankAccountNum != null) {
                i.setBankAccount(new BankAccount());
                i.getBankAccount().setAccountNumber(depositedBankAccountNum);
            }
        }
        InstrumentRequest request = new InstrumentRequest();
        request.setInstruments(instruments);
        final RequestInfo requestinfo = new RequestInfo();

        requestinfo.setAuthToken(getUserToken());

        request.setRequestInfo(requestinfo);

        return restTemplate.postForObject(url.toString(), request, InstrumentResponse.class);
    }

    public InstrumentResponse depositeInstruments(List<Instrument> instruments, String depositedBankAccountNum) {

        final StringBuilder url = new StringBuilder(hostUrl + instrumentUpdateUrl);
        FinancialStatus instrumentStatusReconciled = getInstrumentStatusByCode("Deposited");
        for (Instrument i : instruments) {
            i.setFinancialStatus(instrumentStatusReconciled);
            i.setBankAccount(new BankAccount());
            i.getBankAccount().setAccountNumber(depositedBankAccountNum);
        }
        InstrumentRequest request = new InstrumentRequest();
        request.setInstruments(instruments);
        final RequestInfo requestinfo = new RequestInfo();

        requestinfo.setAuthToken(getUserToken());

        request.setRequestInfo(requestinfo);

        return restTemplate.postForObject(url.toString(), request, InstrumentResponse.class);
    }

    public InstrumentResponse reconcileInstrumentsWithPayinSlipId(List<Instrument> instruments, String depositedBankAccountNum,
            String payinSlipId) {

        final StringBuilder url = new StringBuilder(hostUrl + instrumentUpdateUrl);
        FinancialStatus instrumentStatusReconciled = getInstrumentStatusByCode("Reconciled");
        for (Instrument i : instruments) {
            i.setFinancialStatus(instrumentStatusReconciled);
            i.setBankAccount(new BankAccount());
            i.getBankAccount().setAccountNumber(depositedBankAccountNum);
            i.setPayinSlipId(payinSlipId);
        }

        InstrumentRequest request = new InstrumentRequest();
        request.setInstruments(instruments);
        final RequestInfo requestinfo = new RequestInfo();

        requestinfo.setAuthToken(getUserToken());

        request.setRequestInfo(requestinfo);

        return restTemplate.postForObject(url.toString(), request, InstrumentResponse.class);
    }

    public RemittanceResponse createRemittance(List<Remittance> remittanceList) {

        final StringBuilder url = new StringBuilder(hostUrl + remittanceCreateUrl);
        RemittanceRequest request = new RemittanceRequest();
        request.setRemittances(remittanceList);
        final RequestInfo requestInfo = new RequestInfo();

        requestInfo.setAuthToken(getUserToken());
        requestInfo.setUserInfo(new UserInfo());
        requestInfo.getUserInfo().setId(ApplicationThreadLocals.getUserId());
        request.setRequestInfo(requestInfo);

        return restTemplate.postForObject(url.toString(), request, RemittanceResponse.class);
    }

    public ReceiptResponse updateReceipts(List<Receipt> receiptList) {

        final StringBuilder url = new StringBuilder(hostUrl + receiptUpdateUrl);
        ReceiptRequest request = new ReceiptRequest();
        request.setReceipt(receiptList);
        final RequestInfo requestinfo = new RequestInfo();

        requestinfo.setAuthToken(getUserToken());

        request.setRequestInfo(requestinfo);

        return restTemplate.postForObject(url.toString(), request, ReceiptResponse.class);
    }

    public List<Task> getTasks() {

        List<Task> tasks = new ArrayList<>();
        if (isNotBlank(workflowServiceUrl)) {
            final RestTemplate restTemplate = new RestTemplate();
            TaskResponse tresp;
            try {
                RequestInfo createRequestInfo = createRequestInfo();
                RequestInfoWrapper requestInfo = new RequestInfoWrapper();
                requestInfo.setRequestInfo(createRequestInfo);
                LOGGER.info("call:" + workflowServiceUrl);
                tresp = restTemplate.postForObject(workflowServiceUrl, requestInfo, TaskResponse.class);
                tasks = tresp.getTasks();
            } catch (final Exception e) {
                final String errMsg = "Exception while getting inbox items from microservice ";
                // throw new ApplicationRuntimeException(errMsg, e);
                LOGGER.fatal(errMsg, e);
            }
        }
        return tasks;
    }

    public List<Inbox> getInboxItems() {
        List<Inbox> inboxItems = new LinkedList<>();
        if (hasWorkflowService())
            for (Task t : getTasks()) {
                Inbox inboxItem = new Inbox();
                inboxItem.setId(t.getId());
                inboxItem.setCreatedDate(t.getCreatedDate());
                inboxItem.setDate(toDefaultDateTimeFormat(t.getCreatedDate()));
                inboxItem.setSender(t.getSenderName());
                inboxItem.setTask(t.getNatureOfTask());
                inboxItem.setStatus(t.getStatus());
                inboxItem.setDetails(t.getDetails());
                inboxItem.setLink(t.getUrl());
                inboxItem.setSender(t.getSenderName());
                inboxItems.add(inboxItem);
            }
        return inboxItems;
    }

    public boolean hasWorkflowService() {
        return isNotBlank(workflowServiceUrl);
    }

    public void saveAuthToken(String auth_token, String sessionId) {
        redisTemplate.opsForHash().putIfAbsent(auth_token, sessionId, sessionId);
    }

    public String readSesionIdByAuthToken(String auth_token) {

        if (redisTemplate.hasKey(auth_token)) {
            return (String) redisTemplate.opsForValue().get(auth_token);
        }
        return null;
    }

    public void SaveSessionToRedis(String access_token, String sessionId, Map<String, String> values) {

        if (null != access_token && null != values && values.size() > 0) {
            values.keySet().forEach(key -> {
                redisTemplate.opsForHash().putIfAbsent(sessionId, key, values.get(key));
            });
            redisTemplate.opsForList().leftPush(access_token, sessionId);
        }

    }

    public void savetoRedis(String sessionId, String key, Object obj) {
        redisTemplate.opsForHash().putIfAbsent(sessionId, key, obj);
    }

    public void setExpire(String key) {
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    public Object readFromRedis(String sessionId, String key) {
        if (redisTemplate.hasKey(sessionId))
            return redisTemplate.opsForHash().get(sessionId, key);
        else
            return null;
    }

    public void removeSessionFromRedis(String access_token, String sessionId) {
        LOGGER.info("Logout for authtoken : " + access_token +" and session : "+sessionId);
        if (null != access_token && redisTemplate.hasKey(access_token)){
            sessionId = (String)redisTemplate.opsForHash().get(sessionId, sessionId);
            if (sessionId != null) {
                System.out.println("***********sessionId**** " + sessionId);
                redisTemplate.delete(sessionId);
                System.out.println("spring:session:sessions:" + sessionId);
                System.out.println("spring:session:sessions:expires:" + sessionId);
                redisTemplate.delete("spring:session:sessions:" + sessionId);
                redisTemplate.delete("spring:session:sessions:expires:" + sessionId);
                redisTemplate.opsForHash().delete(access_token,sessionId);
            } else
                LOGGER.info("session not found in redis for : " + access_token);
        }else{
            LOGGER.info("authtoken not found in redis : " + access_token);
        }
    }

    public void refreshToken(String oldToken, String newToken) {
        LOGGER.info("Refresh Token is called OLD::NEW" + oldToken + " :: " + newToken);
        if (redisTemplate.hasKey(oldToken)) {

            while (redisTemplate.opsForList().size(oldToken) > 0) {

                Object sessionId = redisTemplate.opsForList().leftPop(oldToken);
                if (redisTemplate.hasKey(sessionId))
                    if (oldToken.equals(redisTemplate.opsForHash().get(sessionId, "ACCESS_TOKEN"))) {
                        redisTemplate.opsForHash().delete(sessionId, "ACCESS_TOKEN");
                        redisTemplate.opsForHash().put(sessionId, "ACCESS_TOKEN", newToken);
                        redisTemplate.delete(oldToken);
                        redisTemplate.opsForValue().set(newToken, sessionId);
                    }
                redisTemplate.opsForList().leftPush(newToken, sessionId);
            }
            redisTemplate.delete(oldToken);

        }
    }

    public static ResponseInfo getResponseInfo(RequestInfo requestInfo, Integer status, String apiId) {
        ResponseInfo info = new ResponseInfo();

        if (requestInfo != null) {
            info.setVer(requestInfo.getVer());
            info.setResMsgId(requestInfo.getMsgId());
            info.setApiId(requestInfo.getApiId());
        } else if (apiId == null)
            info.setApiId(apiId);

        if (status != null)
            info.setStatus(status.toString());
        else
            Log.error("Code is sending null value for status");
        info.setTs(new Date().toString());
        return info;
    }
    
    public static Long getEpochDate(Date date){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC);
//        Date date = new Date();  
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String strDate = dateFormat.format(date);
        long epoch = Instant.from(fmt.parse(strDate)).toEpochMilli();
        return epoch;
    }
    
    public void pushDataToIndexer(Object data){
        Object postForObject = restTemplate.postForObject(egovIndexerUrl, data, Object.class, finIndexerTopic);
    }
    
    public Object getMdmsData(List<ModuleDetail> moduleDetails,boolean isStateLevel){
        String mdmsUrl = this.hostUrl + this.mdmsSearchUrl;
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setAuthToken(getUserToken());
        MdmsCriteria mdmscriteria = new MdmsCriteria();
        if(isStateLevel){
            mdmscriteria.setTenantId(getTenentId().split(Pattern.quote("."))[0]);
        }else{
            mdmscriteria.setTenantId(getTenentId());
        }
        mdmscriteria.setModuleDetails(moduleDetails);
        MdmsCriteriaReq mdmsrequest = new MdmsCriteriaReq();
        mdmsrequest.setRequestInfo(requestInfo);
        mdmsrequest.setMdmsCriteria(mdmscriteria);
        return restTemplate.postForObject(mdmsUrl, mdmsrequest, Map.class);
    }
    
    public String getHeaderNameForTenant(){
        String ulbGrade = "";
        List<ModuleDetail> moduleDetailList = new ArrayList<>();
        String tenentId = getTenentId();
        try {
            this.prepareModuleDetails(moduleDetailList, "tenant", "tenants", "code", tenentId);
            Map postForObject = mapper.convertValue(this.getMdmsData(moduleDetailList, true), Map.class);
            if(postForObject != null){
                ulbGrade = mapper.convertValue(JsonPath.read(postForObject, "$.MdmsRes.tenant.tenants[0].city.ulbGrade"),String.class);
            }
            if(ulbGrade != null && !ulbGrade.isEmpty())
                ulbGrade = environment.getProperty(ulbGrade.replaceAll(" ", ""));
        } catch (Exception e) {
            LOGGER.error("ERROR occurred while fetching header name of tenant in getHeaderNameForTenant : ",e);
        }
        return tenentId.split(Pattern.quote("."))[1]+" "+(ulbGrade != null ? ulbGrade : "");
    }
    
    private void prepareModuleDetails(List<ModuleDetail> moduleDetailsList,String moduleNme,String masterName,String filterKey, String filterValue){
        List<MasterDetail> masterDetails = new ArrayList<>();
        ListIterator<ModuleDetail> listIterator = moduleDetailsList.listIterator();
        while(listIterator.hasNext()){
             ModuleDetail existModName = listIterator.next();
            if(existModName.equals(moduleNme)){
                this.prepareMasterDetails(existModName.getMasterDetails(), masterName, filterKey, filterValue);
                listIterator.add(existModName);
                return;
            }
        }
        this.prepareMasterDetails(masterDetails , masterName, filterKey, filterValue);
        moduleDetailsList.add(new ModuleDetail(moduleNme, masterDetails));
    }
    
    private void prepareMasterDetails(List<MasterDetail> masterDetailList,String masterName,String filterKey,String filterValue){
        for(MasterDetail md : masterDetailList){
            if(md.getName().equals(masterName))
                break;
        }
        StringBuilder filterBuilder = null;
        if(filterKey != null && filterValue != null){
            filterBuilder = new StringBuilder();
            filterBuilder.append("[?(@.").append(filterKey).append(" in [").append(filterValue).append("])]");
        }
        masterDetailList.add(new MasterDetail(masterName, filterBuilder.toString()));
    }
    
    public String getBusinessServiceNameByCode(String code){
        String serviceName = "";
        List<ModuleDetail> moduleDetailsList = new ArrayList<>();
        try {
            this.prepareModuleDetails(moduleDetailsList , "BillingService", "BusinessService", "code", code);
            Map postForObject = mapper.convertValue(this.getMdmsData(moduleDetailsList, true), Map.class);
            if(postForObject != null){
                serviceName = mapper.convertValue(JsonPath.read(postForObject, "$.MdmsRes.BillingService.BusinessService[0].businessService"),String.class);
            }
        } catch (Exception e) {
            LOGGER.error("ERROR occurred while fetching business service details in getBusinessServiceNameByCode method: ",e);
        }
        return serviceName.isEmpty() ? code : serviceName;
    }
    
    public List<BusinessService> getBusinessService(String type) {
        List<BusinessService> list = null;
        List<ModuleDetail> moduleDetailsList = new ArrayList<>();
        this.prepareModuleDetails(moduleDetailsList, "BillingService", "BusinessService", "type", type);
        Map postForObject = mapper.convertValue(this.getMdmsData(moduleDetailsList, true), Map.class);
        if(postForObject != null){
             list = mapper.convertValue(JsonPath.read(postForObject, "$.MdmsRes.BillingService.BusinessService"),new TypeReference<List<BusinessService>>(){});
        }
        return list;
    }
    
    public List<TaxHeadMaster> getTaxheadsByServiceCode(String serviceCode) {
        List<TaxHeadMaster> list = null;
        List<ModuleDetail> moduleDetailsList = new ArrayList<>();
        this.prepareModuleDetails(moduleDetailsList, "BillingService", "TaxHeadMaster", "service", serviceCode);
        Map postForObject = mapper.convertValue(this.getMdmsData(moduleDetailsList, true), Map.class);
        if(postForObject != null){
             list = mapper.convertValue(JsonPath.read(postForObject, "$.MdmsRes.BillingService.TaxHeadMaster"),new TypeReference<List<TaxHeadMaster>>(){});
        }
        return list;
    }
}

class FilterRequest {
    private List<Long> id;
    
    private String name;

    private String code;

    @Size(min=8, max=64)
    @JsonProperty("names")
    private List<String> names;

    @Size(min=1, max=10)
    @JsonProperty("codes")
    private List<String> codes;

    private Boolean active;

    @NotNull
    private String tenantId;

    private String sortBy;

    private String sortOrder;

    @Min(1)
    @Max(500)
    private Short pageSize;

    private Short pageNumber;

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Short getPageSize() {
        return pageSize;
    }

    public void setPageSize(Short pageSize) {
        this.pageSize = pageSize;
    }

    public Short getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Short pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    
}