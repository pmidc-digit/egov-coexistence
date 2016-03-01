/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It
	   is required that all modified versions of this material be marked in
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program
	   with regards to rights under trademark law for use of the trade names
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.collection.web.actions.receipts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.collection.constants.CollectionConstants;
import org.egov.commons.Accountdetailkey;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.service.EntityTypeService;
import org.egov.commons.utils.EntityType;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.models.ServiceAccountDetails;
import org.egov.infstr.models.ServiceDetails;
import org.egov.infstr.models.ServiceSubledgerInfo;
import org.hibernate.HibernateException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@ParentPackage("egov")
@Results({ @Result(name = "schemeList", location = "ajaxReceiptCreate-schemeList.jsp"),
        @Result(name = "subSchemeList", location = "ajaxReceiptCreate-subSchemeList.jsp"),
        @Result(name = "serviceList", location = "ajaxReceiptCreate-serviceList.jsp"),
        @Result(name = "serviceAccDtls", location = "ajaxReceiptCreate-serviceAccDtls.jsp"),
        @Result(name = "subledger", location = "ajaxReceiptCreate-subledger.jsp"),
        @Result(name = "entities", location = "ajaxReceiptCreate-entities.jsp"),
        @Result(name = AjaxReceiptCreateAction.RESULT, location = "ajaxReceiptCreate-result.jsp") })
public class AjaxReceiptCreateAction extends BaseFormAction {
    private static final long serialVersionUID = 1L;
    private static final String DETAILTYPEID = "detailtypeid";
    protected static final String RESULT = "result";
    private String value;
    private List<EntityType> entityList;
    private static final String accountDetailTypeQuery = " from Accountdetailtype where id=?";
    private List<Scheme> schemeList;
    private List<SubScheme> subSchemes;
    private List<ServiceDetails> serviceList;
    private List<ServiceAccountDetails> accountDetails;
    private List<ServiceSubledgerInfo> subledgerDetails;

    public String getAccountForService() {
        setValue(CollectionConstants.BLANK);
        final String serviceId = parameters.get("serviceId")[0];
        final String queryString = "select sd.serviceAccount from ServiceDetails sd where sd.id='" + serviceId + "'";
        final List<CChartOfAccounts> list = getPersistenceService().findAllBy(queryString);
        for (final CChartOfAccounts accounts : list)
            value += accounts.getId().toString() + "~" + accounts.getGlcode() + "~" + accounts.getName() + "#";

        return RESULT;
    }

    /*
     * public String getMISdetailsForService() { value = ""; String serviceId =
     * parameters.get("serviceId")[0]; String queryString =
     * "select nvl(fund.id,'-1'),nvl(department.id,'-1') from ServiceDetails where id='"
     * + serviceId + "'"; List<Object[]> list =
     * getPersistenceService().findAllBy(queryString); if (list != null &&
     * !list.isEmpty()) { for (int i = 0; i < list.size(); i++) { Object[]
     * arrayObjectInitialIndex = list.get(i); value =
     * arrayObjectInitialIndex[0].toString() + "~" +
     * arrayObjectInitialIndex[1].toString() + "#"; } } return RESULT; }
     */

    public String getMISdetailsForService() {
        value = "";
        final String serviceId = parameters.get("serviceId")[0];

        final ServiceDetails service = (ServiceDetails) getPersistenceService().find(
                " from ServiceDetails where id=? ", Long.valueOf(serviceId));

        if (null != service)
            for (final Department department : service.getServiceDept())
                value = (null != service.getFund() ? service.getFund().getId() : -1) + "~"
                        + (null != department ? department.getId() : -1) + "#";

        return RESULT;
    }

    @Action(value = "/receipts/ajaxReceiptCreate-getDetailCode")
    public String getDetailCode() throws Exception {
        value = "";
        final String accountCodes = parameters.get("accountCodes")[0];
        final String arr[] = accountCodes.split(",");

        for (final String element : arr) {
            final CChartOfAccountDetail chartOfAccountDetail = (CChartOfAccountDetail) getPersistenceService().find(
                    " from CChartOfAccountDetail" + " where glCodeId=(select id from CChartOfAccounts where glcode=?)",
                    element);

            if (null != chartOfAccountDetail)
                value += element + "~" + chartOfAccountDetail.getGlCodeId().getId().toString() + "~";
        }
        if (StringUtils.isNotBlank(value))
            value = value.substring(0, value.length() - 1);
        return RESULT;
    }

    @Action(value = "/receipts/ajaxReceiptCreate-getDetailType")
    public String getDetailType() throws Exception {
        value = "";
        final String accountCode = parameters.get("accountCode")[0];
        final String index = parameters.get("index")[0];
        final String selectedDetailType = parameters.get("selectedDetailType")[0];
        final String onload = parameters.get("onload")[0];
        final List<Accountdetailtype> list = getPersistenceService()
                .findAllBy(
                        " from Accountdetailtype"
                                + " where id in (select detailTypeId from CChartOfAccountDetail where glCodeId=(select id from CChartOfAccounts where glcode=?))  ",
                        accountCode);
        if (list == null || list.isEmpty())
            value = index + "~" + ERROR + "#";
        else
            for (final Accountdetailtype accountdetailtype : list)
                value = value + index + "~" + selectedDetailType + "~" + onload + "~" + accountdetailtype.getName()
                + "~" + accountdetailtype.getId().toString() + "#";
        if (StringUtils.isNotBlank(value))
            value = value.substring(0, value.length() - 1);
        return RESULT;
    }

    /**
     * This method is accessed from challan.js and MiscReceipts.js
     *
     * @return
     * @throws Exception
     */
    @Action(value = "/receipts/ajaxReceiptCreate-ajaxValidateDetailCodeNew")
    public String ajaxValidateDetailCodeNew() throws Exception {
        final String code = parameters.get("code")[0];
        final String index = parameters.get("index")[0];
        final String codeorname = parameters.get("codeorname")[0];

        final Accountdetailtype adt = (Accountdetailtype) getPersistenceService().find(accountDetailTypeQuery,
                Integer.valueOf(parameters.get(DETAILTYPEID)[0]));
        if (adt == null) {
            value = index + "~" + ERROR + "#";
            return RESULT;
        }

        final String table = adt.getFullQualifiedName();
        final Class<?> service = Class.forName(table);
        String simpleName = service.getSimpleName();
        simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1) + "Service";

        final WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(ServletActionContext
                .getServletContext());
        final EntityTypeService entityService = (EntityTypeService) wac.getBean(simpleName);
        entityList = (List<EntityType>) entityService.filterActiveEntities(code, -1, adt.getId());

        if (entityList == null || entityList.isEmpty())
            value = index + "~" + ERROR + "#";
        else {
            if (entityList.size() > 1) {// To Check with same code/name if more
                // than one entity is returned
                value = index + "~" + ERROR + "#";
                return RESULT;
            }
            for (final EntityType entity : entityList)
                if (entity == null) {
                    value = index + "~" + ERROR + "#";
                    break;
                } else if (codeorname.equalsIgnoreCase("both")) {// To Check if
                    // both name
                    // and code has
                    // to be
                    // compared
                    if (entity.getName().equals(code) || entity.getCode().equals(code)) {
                        value = index + "~" + entity.getEntityId() + "~" + entity.getName() + "~" + entity.getCode();
                        break;
                    } else
                        value = index + "~" + ERROR + "#";
                } else if (entity.getCode().equals(code)) {// In case of view
                    // mode, to get the
                    // details from the
                    // code
                    value = index + "~" + entity.getEntityId() + "~" + entity.getName() + "~" + entity.getCode();
                    break;
                } else
                    value = index + "~" + ERROR + "#";
        }

        return RESULT;
    }

    @Action(value = "/receipts/ajaxReceiptCreate-getCodeNew")
    public String getCodeNew() throws Exception {
        value = "";
        final String detailTypeId = parameters.get("detailTypeId")[0];
        final String filterKey = parameters.get("filterKey")[0];
        final Integer accountDetailTypeId = Integer.valueOf(detailTypeId);
        final Accountdetailtype adt = (Accountdetailtype) getPersistenceService().find(accountDetailTypeQuery,
                accountDetailTypeId);
        if (adt == null)
            return RESULT;
        final String table = adt.getFullQualifiedName();
        final Class<?> service = Class.forName(table);
        String simpleName = service.getSimpleName();
        simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1) + "Service";

        final WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(ServletActionContext
                .getServletContext());
        final EntityTypeService entityService = (EntityTypeService) wac.getBean(simpleName);
        final List<EntityType> tempEntityList = (List<EntityType>) entityService.filterActiveEntities(filterKey, -1,
                adt.getId());
        entityList = new ArrayList<EntityType>();
        for (final EntityType e : tempEntityList) {
            if (e.getName().contains("@") || e.getName().contains("#") || e.getName().contains("$")
                    || e.getName().contains("%") || e.getName().contains("^") || e.getName().contains("&")
                    || e.getName().contains("*"))
                e.getName().replace("@", " ").replace("#", " ").replace("$", " ").replace("%", " ").replace("^", " ")
                .replace("&", " ").replace("*", " ");
            entityList.add(e);
        }
        return "entities";
    }

    /*
     * This code has to be deleted once autocomplete feature is changed in misc
     * Receipts also
     */
    @Action(value = "/receipts/ajaxReceiptCreate-ajaxValidateDetailCode")
    public String ajaxValidateDetailCode() {
        final String code = parameters.get("code")[0];
        final String index = parameters.get("index")[0];
        try {

            final Accountdetailtype adt = (Accountdetailtype) getPersistenceService().find(accountDetailTypeQuery,
                    Integer.valueOf(parameters.get(DETAILTYPEID)[0]));
            if (adt == null) {
                value = index + "~" + ERROR + "#";
                return RESULT;
            }

            final List<EntityType> entityList = getPersistenceService().findAllBy(
                    " from " + adt.getFullQualifiedName() + ""
                            + " where id in (select detailkey from Accountdetailkey where accountdetailtype.id=?)  ",
                            Integer.valueOf(parameters.get(DETAILTYPEID)[0]));

            if (getEntityList() == null || getEntityList().isEmpty())
                value = index + "~" + ERROR + "#";
            else
                for (final EntityType entity : entityList)
                    if (entity == null) {
                        value = index + "~" + ERROR + "#";
                        break;
                    } else if (entity.getCode().equals(code)) {
                        final Accountdetailkey key = (Accountdetailkey) getPersistenceService().find(
                                " from Accountdetailkey where accountdetailtype.id=? and detailkey=? ",
                                Integer.valueOf(parameters.get(DETAILTYPEID)[0]), entity.getEntityId());
                        if (key == null)
                            value = index + "~" + ERROR + "#";
                        else
                            value = index + "~" + key.getId() + "~" + entity.getName();
                        break;
                    } else
                        value = index + "~" + ERROR + "#";

        } catch (final HibernateException e) {
            value = index + "~" + ERROR + "#";
        } catch (final Exception e) {
            value = index + "~" + ERROR + "#";
        }
        return RESULT;
    }

    public String getCode() throws Exception {
        value = "";
        final String detailTypeId = parameters.get("detailTypeId")[0];
        final Accountdetailtype adt = (Accountdetailtype) getPersistenceService().find(accountDetailTypeQuery,
                Integer.valueOf(detailTypeId));

        if (adt == null)
            return RESULT;

        setEntityList(getPersistenceService().findAllBy(
                "select entity from " + adt.getFullQualifiedName() + " entity,Accountdetailkey adk"
                        + " where entity.id =adk.detailkey and adk.accountdetailtype.id=? ",
                        Integer.valueOf(detailTypeId)));

        return "entities";
    }

    public String getDetailTypeForService() throws Exception {

        value = "";
        final String accountCode = parameters.get("accountCode")[0];
        final String index = parameters.get("index")[0];

        final List<Accountdetailtype> list = getPersistenceService()
                .findAllBy(
                        " from Accountdetailtype"
                                + " where id in (select detailTypeId from CChartOfAccountDetail where glCodeId=(select id from CChartOfAccounts where glcode=?))  ",
                        accountCode);

        for (final Accountdetailtype accountdetailtype : list)
            value = value + index + "~" + accountdetailtype.getDescription() + "~"
                    + accountdetailtype.getId().toString() + "#";
        if (!value.equals(""))
            value = value.substring(0, value.length() - 1);

        return "result";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/receipts/ajaxReceiptCreate-ajaxLoadSchemes")
    public String ajaxLoadSchemes() {

        final Integer fundId = Integer.valueOf(parameters.get("fundId")[0]);
        if (null == fundId || fundId == -1)
            schemeList = getPersistenceService().findAllBy(
                    " from Scheme where fund.id=? and isActive=true order by name", -1);
        else
            schemeList = getPersistenceService().findAllBy(
                    " from Scheme where fund.id=? and isActive=true order by name", fundId);

        return "schemeList";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/receipts/ajaxReceiptCreate-ajaxLoadSubSchemes")
    public String ajaxLoadSubSchemes() {
        final Integer schemeId = Integer.valueOf(parameters.get("schemeId")[0]);
        if (null != schemeId && schemeId != -1)
            subSchemes = getPersistenceService().findAllBy(
                    "from SubScheme where scheme.id=? and isActive=true order by name", schemeId);
        else
            subSchemes = Collections.EMPTY_LIST;

        return "subSchemeList";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/receipts/ajaxReceiptCreate-ajaxLoadServiceByCategory")
    public String ajaxLoadServiceByCategory() {

        if (null != parameters.get("serviceCatId") && null != parameters.get("serviceCatId")[0]
                && Integer.valueOf(parameters.get("serviceCatId")[0]) != -1)
            serviceList = getPersistenceService().findAllByNamedQuery("SERVICE_BY_CATEGORY_FOR_TYPE",
                    Long.valueOf(parameters.get("serviceCatId")[0]), CollectionConstants.SERVICE_TYPE_COLLECTION,
                    Boolean.TRUE);
        else
            serviceList = Collections.EMPTY_LIST;

        return "serviceList";

    }

    @Action(value = "/receipts/ajaxReceiptCreate-ajaxFinMiscDtlsByService")
    public String ajaxFinMiscDtlsByService() {

        final Long serviceId = Long.valueOf(parameters.get("serviceId")[0]);
        final Long deptId = Long.valueOf(parameters.get("deptId")[0]);
        final ServiceDetails service = (ServiceDetails) getPersistenceService().find(
                "from ServiceDetails service  inner join fetch service.serviceDept dept where dept.id=? and "
                        + "  service.isEnabled=true" + " and service.id=?", deptId, serviceId);

        final StringBuffer miscDetails = new StringBuffer();
        if (null != service)
            miscDetails.append(null != service.getFund() ? service.getFund().getId() : "-1").append('~') // fund
                    .append(null != service.getScheme() ? service.getScheme().getId() : "-1").append('~') // scheme
                    .append(null != service.getSubscheme() ? service.getSubscheme().getId() : "-1").append('~') // subscheme
                    .append(null != service.getFundSource() ? service.getFundSource().getId() : "-1").append('~') // fundsource
                    .append(null != service.getFunctionary() ? service.getFunctionary().getId() : "-1"); // functionary
        else
            miscDetails.append("-1").append('~') // fund
                    .append("-1").append('~') // scheme
                    .append("-1").append('~') // subscheme
                    .append("-1").append('~') // fundsource
                    .append("-1"); // functionary
        value = miscDetails.toString();
        return "result";

    }

    @Action(value = "/receipts/ajaxReceiptCreate-ajaxFinAccDtlsByService")
    public String ajaxFinAccDtlsByService() {

        final Long serviceId = Long.valueOf(parameters.get("serviceId")[0]);
        final Long deptId = Long.valueOf(parameters.get("deptId")[0]);
        final ServiceDetails service = (ServiceDetails) getPersistenceService().find(
                "from ServiceDetails service  inner join fetch service.serviceDept dept where dept.id=? and "
                        + " service.id=?", deptId, serviceId);

        accountDetails = new ArrayList<ServiceAccountDetails>();

        if (null != service)
            accountDetails.addAll(service.getServiceAccountDtls());
        else
            accountDetails.addAll(Collections.EMPTY_LIST);

        return "serviceAccDtls";

    }

    @Action(value = "/receipts/ajaxReceiptCreate-ajaxFinSubledgerByService")
    public String ajaxFinSubledgerByService() {
        final Long serviceId = Long.valueOf(parameters.get("serviceId")[0]);
        final Long deptId = Long.valueOf(parameters.get("deptId")[0]);
        final ServiceDetails service = (ServiceDetails) getPersistenceService().find(
                "from ServiceDetails service  inner join fetch service.serviceDept dept where dept.id=? and "
                        + " service.id=?", deptId, serviceId);
        subledgerDetails = new ArrayList<ServiceSubledgerInfo>();
        ServiceSubledgerInfo servicInfo;
        if (null != service)
            for (final ServiceAccountDetails account : service.getServiceAccountDtls()) {
                subledgerDetails.addAll(account.getSubledgerDetails());
                if (subledgerDetails.isEmpty()) {
                    final CChartOfAccountDetail chartOfAccountDetail = (CChartOfAccountDetail) getPersistenceService()
                            .find("from CChartOfAccountDetail cd where cd.glCodeId.id=?", account.getGlCodeId().getId());
                    servicInfo = new ServiceSubledgerInfo();
                    if (chartOfAccountDetail != null) {
                        servicInfo.setDetailType(chartOfAccountDetail.getDetailTypeId());
                        servicInfo.setServiceAccountDetail(account);
                        subledgerDetails.add(servicInfo);
                    } else
                        subledgerDetails.addAll(Collections.EMPTY_LIST);
                }

            }

        return "subledger";
    }

    @Override
    public Object getModel() {
        return null;
    }

    public List<EntityType> getEntityList() {
        return entityList;
    }

    public void setEntityList(final List<EntityType> entityList) {
        this.entityList = entityList;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public List<Scheme> getSchemeList() {
        return schemeList;
    }

    public void setSchemeList(final List<Scheme> schemeList) {
        this.schemeList = schemeList;
    }

    public List<SubScheme> getSubSchemes() {
        return subSchemes;
    }

    public void setSubSchemes(final List<SubScheme> subSchemes) {
        this.subSchemes = subSchemes;
    }

    public List<ServiceDetails> getServiceList() {
        return serviceList;
    }

    public List<ServiceAccountDetails> getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(final List<ServiceAccountDetails> accountDetails) {
        this.accountDetails = accountDetails;
    }

    public List<ServiceSubledgerInfo> getSubledgerDetails() {
        return subledgerDetails;
    }

    public void setSubledgerDetails(final List<ServiceSubledgerInfo> subledgerDetails) {
        this.subledgerDetails = subledgerDetails;
    }

}
