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

package org.egov.collection.web.actions.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.Bank;
import org.egov.commons.Bankaccount;
import org.egov.commons.dao.BankBranchHibernateDAO;
import org.egov.commons.dao.BankHibernateDAO;
import org.egov.commons.dao.BankaccountHibernateDAO;
import org.egov.commons.entity.BankAccountServiceMap;
import org.egov.infra.microservice.models.BankAccountServiceMapping;
import org.egov.infra.microservice.models.BusinessDetails;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPageExt;
import org.egov.infstr.models.ServiceDetails;
import org.egov.infstr.services.PersistenceService;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;

@ParentPackage("egov")
@Results({
        @Result(name = ServiceTypeToBankAccountMappingAction.NEW, location = "serviceTypeToBankAccountMapping-new.jsp"),
        @Result(name = ServiceTypeToBankAccountMappingAction.SUCCESS, location = "serviceTypeToBankAccountMapping-success.jsp"),
        @Result(name = ServiceTypeToBankAccountMappingAction.INDEX, location = "serviceTypeToBankAccountMapping-index.jsp") })
public class ServiceTypeToBankAccountMappingAction extends BaseFormAction {
    private static final long serialVersionUID = 1L;
    private static final String BANK_BRANCH_LIST = "bankBranchList";
    private static final String BANK_NAME_LIST = "bankNameList";
    private static final String BANK_ACCOUNT_LIST = "bankAccountIdList";
    private PersistenceService<BankAccountServiceMap, Long> bankAccountMappingService;
    private BankAccountServiceMap bankAccountServiceMap = new BankAccountServiceMap();
    @Autowired
    private BankHibernateDAO bankHibernateDAO;
    @Autowired
    private BankBranchHibernateDAO bankBrankHibernateDAO;
    @Autowired
    private BankaccountHibernateDAO bankAccountHibernateDAO;
    private List<BankAccountServiceMap> bankAccountServices = new ArrayList<>();
    private List<BankAccountServiceMapping> mappings = new ArrayList<>();
    private Integer bankId;
    private Integer branchId;
    private String serviceCategory;
    private Long serviceAccountId;
    private String sourcePage;
    private String target;
    private Integer fundId;

    public ServiceTypeToBankAccountMappingAction() {
        addRelatedEntity("serviceDetails", ServiceDetails.class);
        addRelatedEntity("bankAccountId", Bankaccount.class);
    }

    @Action(value = "/service/serviceTypeToBankAccountMapping-newform")
    public String newform() {
        if (getServiceAccountId() != null) {
            populateListsForView();
            setupDropdownDataExcluding();
            bankAccountServiceMap = bankAccountMappingService.findById(getServiceAccountId(), false);
            addDropdownData(
                    BANK_BRANCH_LIST,
                    bankBrankHibernateDAO.getAllBankBranchsByBank(bankAccountServiceMap.getBankAccountId()
                            .getBankbranch().getBank().getId()));
            addDropdownData(
                    BANK_ACCOUNT_LIST,
                    bankAccountHibernateDAO.getBankAccountByBankBranchForReceiptsPayments(bankAccountServiceMap
                            .getBankAccountId().getBankbranch().getId(), bankAccountServiceMap.getServiceDetails()
                                    .getFund().getId()));
            setServiceCategory(bankAccountServiceMap.getServiceDetails().getServiceCategory().getCode());
            setBankId(bankAccountServiceMap.getBankAccountId().getBankbranch().getBank().getId());
            setBranchId(bankAccountServiceMap.getBankAccountId().getBankbranch().getId());
        } else
            populateLists();
        return NEW;
    }

    private void populateLists() {
        addDropdownData("serviceCategoryList", microserviceUtils.getBusinessCategories());
        addDropdownData("serviceDetailsList", Collections.emptyList());
        addDropdownData(BANK_NAME_LIST, bankHibernateDAO.getAllBankHavingBranchAndAccounts());
        addDropdownData(BANK_BRANCH_LIST, Collections.emptyList());
        addDropdownData(BANK_ACCOUNT_LIST, Collections.emptyList());
    }

    private void populateListsForView() {
        addDropdownData("serviceCategoryList", microserviceUtils.getBusinessCategories());
        if (serviceCategory != null && !serviceCategory.isEmpty() && !serviceCategory.equalsIgnoreCase("-1"))
            addDropdownData("serviceDetailsList", microserviceUtils.getBusinessDetailsByCategoryCode(serviceCategory));
        else
            addDropdownData("serviceDetailsList", Collections.emptyList());
        if (sourcePage != null && "modify".equals(sourcePage))
            addDropdownData(BANK_NAME_LIST, bankHibernateDAO.getAllBankHavingBranchAndAccounts());
        else
            addDropdownData(BANK_NAME_LIST, getBankMappedToService());
        addDropdownData(
                BANK_BRANCH_LIST,
                bankId != null && bankId != -1 ? bankBrankHibernateDAO
                        .getAllBankBranchsByBankForReceiptPayments(bankId) : Collections.emptyList());
        addDropdownData(
                BANK_ACCOUNT_LIST, getBankAccountMappedToService());
    }

    @SuppressWarnings("unchecked")
    private List<Bank> getBankMappedToService() {
        List<BankAccountServiceMapping> mappings = microserviceUtils.getBankAcntServiceMappings();
        List<String> accountNumbers = new ArrayList<String>();
        for (BankAccountServiceMapping basm : mappings) {
            accountNumbers.add(basm.getBankAccount());
        }
        final String serviceBankQueryString = "select distinct ba.bankbranch.bank from Bankaccount ba where ba.accountnumber in(:accountnumbers))";

        final Query bankListQuery = persistenceService.getSession().createQuery(serviceBankQueryString);
        bankListQuery.setParameterList("accountnumbers", accountNumbers);
        return bankListQuery.list();
    }

    @SuppressWarnings("unchecked")
    private List<Bankaccount> getBankAccountMappedToService() {
        List<BankAccountServiceMapping> mappings = microserviceUtils.getBankAcntServiceMappings();
        List<String> accountNumbers = new ArrayList<String>();
        for (BankAccountServiceMapping basm : mappings) {
            accountNumbers.add(basm.getBankAccount());
        }
        final String serviceBankQueryString = "select distinct ba from Bankaccount ba where ba.accountnumber in(:accountnumbers))";

        final Query bankAccListQuery = persistenceService.getSession().createQuery(serviceBankQueryString);
        bankAccListQuery.setParameterList("accountnumbers", accountNumbers);
        return bankAccListQuery.list();
    }

    @Action(value = "/service/serviceTypeToBankAccountMapping-list")
    public String list() {
        populateListsForView();
        return INDEX;
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/service/serviceTypeToBankAccountMapping-search")
    public String search() {
        populateListsForView();
        final StringBuilder searchkQueryString = new StringBuilder();
        StringBuilder businessDetails = new StringBuilder();

        if (serviceCategory != null && !serviceCategory.isEmpty() && !serviceCategory.equalsIgnoreCase("-1")
                && (bankAccountServiceMap.getServiceDetails() == null
                        || bankAccountServiceMap.getServiceDetails().getCode() == null
                        || bankAccountServiceMap.getServiceDetails().getCode().equalsIgnoreCase("-1"))) {
            List<BusinessDetails> bds = microserviceUtils.getBusinessDetailsByCategoryCode(serviceCategory);
            for (BusinessDetails bd : bds) {
                if (businessDetails.length() > 0) {
                    businessDetails.append(",");
                }
                businessDetails.append(bd.getCode());
            }

        }
        if (bankAccountServiceMap.getServiceDetails() != null && !bankAccountServiceMap.getServiceDetails().getCode().isEmpty()
                && !bankAccountServiceMap.getServiceDetails().getCode().equalsIgnoreCase("-1")) {
            businessDetails = new StringBuilder();
            businessDetails.append(bankAccountServiceMap.getServiceDetails().getCode());
        }
        mappings = microserviceUtils
                .getBankAcntServiceMappingsByBankAcc(bankAccountServiceMap.getBankAccountId().getAccountnumber(),
                        businessDetails.toString());
        populateNames(mappings);
        target = "searchresult";
        return INDEX;
    }

    private void populateNames(List<BankAccountServiceMapping> mappings2) {

        StringBuilder businessDetails = new StringBuilder();

        for (BankAccountServiceMapping bd : mappings2) {
            if (businessDetails.length() > 0) {
                businessDetails.append(",");
            }
            businessDetails.append(bd.getBusinessDetails());
        }

        List<BusinessDetails> businessDetailsList = microserviceUtils.getBusinessDetailsByCode(businessDetails.toString());
        Map<String, String> businessDetailsCodeNameMap = new HashMap<>();

        if (businessDetailsList != null)
            for (BusinessDetails bd : businessDetailsList) {
                businessDetailsCodeNameMap.put(bd.getCode(), bd.getName());
            }

        for (BankAccountServiceMapping r : mappings2) {
            if (r.getBusinessDetails() != null
                    && !r.getBusinessDetails().isEmpty())
                r.setBusinessDetailsName(businessDetailsCodeNameMap.get(r.getBusinessDetails()));
        }
    }

    @ValidationErrorPageExt(action = NEW, makeCall = true, toMethod = "newform")
    @Action(value = "/service/serviceTypeToBankAccountMapping-create")
    public String create() {
        if (bankAccountServiceMap.getBankAccountId().getId() != null)
            bankAccountServiceMap
                    .setBankAccountId(bankAccountHibernateDAO.findById(bankAccountServiceMap.getBankAccountId().getId(), false));
        BankAccountServiceMapping basm = new BankAccountServiceMapping();
        basm.setBusinessDetails(bankAccountServiceMap.getServiceDetails().getCode());
        basm.setBankAccount(bankAccountServiceMap.getBankAccountId().getAccountnumber());
        basm.setBankBranch(bankAccountServiceMap.getBankAccountId().getBankbranch().getBranchcode());
        basm.setBank(bankAccountServiceMap.getBankAccountId().getBankbranch().getBank().getName());
        basm.setTenantId(microserviceUtils.getTenentId());
        microserviceUtils.createBankAcntServiceMappings(basm);
        bankAccountServiceMap.getServiceDetails().setName(
                microserviceUtils.getBusinessDetailsByCode(bankAccountServiceMap.getServiceDetails().getCode()).get(0).getName());
        addActionMessage(getText("service.master.successmessage.create", new String[] {
                bankAccountServiceMap.getServiceDetails().getName(),
                bankAccountServiceMap.getBankAccountId().getBankbranch().getBank().getName(),
                bankAccountServiceMap.getBankAccountId().getAccountnumber() }));
        return SUCCESS;
    }

    public Boolean isMapExists() {
        if (bankAccountServiceMap.getServiceDetails().getId() != null
                && bankAccountServiceMap.getBankAccountId().getId() != null) {
            final BankAccountServiceMap bankAccountServiceMapObj = bankAccountMappingService.find(
                    " from BankAccountServiceMap where serviceDetails.id=? and bankAccountId.id=?",
                    bankAccountServiceMap.getServiceDetails().getId(), bankAccountServiceMap.getBankAccountId().getId());
            if (bankAccountServiceMapObj != null) {
                if (bankAccountServiceMap.getId() == null && bankAccountServiceMapObj.getId() != null) {
                    return true;
                } else if (bankAccountServiceMap.getId() != null
                        && bankAccountServiceMapObj.getId() != bankAccountServiceMap.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object getModel() {
        return bankAccountServiceMap;
    }

    public List<BankAccountServiceMap> getBankAccountServices() {
        return bankAccountServices;
    }

    public void setBankAccountServices(final List<BankAccountServiceMap> bankAccountServices) {
        this.bankAccountServices = bankAccountServices;
    }

    public PersistenceService<BankAccountServiceMap, Long> getBankAccountMappingService() {
        return bankAccountMappingService;
    }

    public void setBankAccountMappingService(
            final PersistenceService<BankAccountServiceMap, Long> bankAccountMappingService) {
        this.bankAccountMappingService = bankAccountMappingService;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(final Integer bankId) {
        this.bankId = bankId;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(final Integer branchId) {
        this.branchId = branchId;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(final String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public Long getServiceAccountId() {
        return serviceAccountId;
    }

    public void setServiceAccountId(final Long serviceAccountId) {
        this.serviceAccountId = serviceAccountId;
    }

    public String getSourcePage() {
        return sourcePage;
    }

    public void setSourcePage(final String sourcePage) {
        this.sourcePage = sourcePage;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public List<BankAccountServiceMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<BankAccountServiceMapping> mappings) {
        this.mappings = mappings;
    }

}
