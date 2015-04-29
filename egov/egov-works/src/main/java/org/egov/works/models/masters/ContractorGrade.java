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
package org.egov.works.models.masters;

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.Min;

import org.egov.infra.persistence.validator.annotation.OptionalPattern;
import org.egov.infra.persistence.validator.annotation.Required;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.egov.infra.validation.regex.Constants;
import org.egov.infstr.ValidationError;
import org.egov.infstr.models.BaseModel;
import org.hibernate.validator.constraints.Length;

@Unique(fields={"grade"},id="id",tableName="EGW_CONTRACTOR_GRADE",columnName={"GRADE"},message="contractorGrade.grade.isunique")
public class ContractorGrade extends BaseModel {
	
	@Required(message="contractorGrade.grade.null")
	@Length(max=20,message="contractorGrade.grade.length")
	@OptionalPattern(regex=Constants.ALPHANUMERIC_WITHSPACE,message="contractorGrade.grade.alphaNumeric")
	private String grade;
	 
	@Required(message="contractorGrade.description.null")
	@Length(max=100,message="contractorGrade.description.length")
	private String description;
	 
	@Required(message="contractorGrade.minAmount.null")
	@Min(value=0, message="contractorGrade.minAmount.valid")
	@OptionalPattern(regex=Constants.NUMERIC,message="contractorGrade.minAmount.numeric")
	private double minAmount;
	
	@Required(message="contractorGrade.maxAmount.null")
	@Min(value=0, message="contractorGrade.maxAmount.valid")
	@OptionalPattern(regex=Constants.NUMERIC,message="contractorGrade.maxAmount.numeric")
	private double maxAmount;
	
	private String maxAmountString;
	private String minAmountString;

	public ContractorGrade() {}
	
	public ContractorGrade(String grade, String description, double minAmount, double maxAmount) {
		this.grade=grade;
		this.description=description;
		this.minAmount=minAmount;
		this.maxAmount=maxAmount;
	}
	
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}

	public double getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	 }

	public String getMaxAmountString() {
		return maxAmountString;
	}

	public void setMaxAmountString(String maxAmountString) {
		this.maxAmountString = maxAmountString;
	}

	public String getMinAmountString() {
		return minAmountString;
	}

	public void setMinAmountString(String minAmountString) {
		this.minAmountString = minAmountString;
	}

	public List<ValidationError> validate()	{
		List<ValidationError> errorList = null;
		if(maxAmount <= minAmount) {
			return Arrays.asList(new ValidationError("maxAmount","contractor.grade.maxamount.invalid"));
		}
		
		 return errorList;
				
	}			
		
}


