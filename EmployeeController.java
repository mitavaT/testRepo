package com.sttl.hrms.HR.empmgmt.empinfo.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.sttl.hrms.HR.empmgmt.empinfo.model.*;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.*;
import com.sttl.hrms.payroll.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmpAddressDtlService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmpEducationQualificationDtlService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmpEmergencyService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmpFamilyService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmpReportingOfficerService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmployeeCategoryService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmployeeService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmployeementTypeService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.JobService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.NomineeService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.RecruitmentTypeService;
import com.sttl.hrms.HR.empmgmt.empinfo.serviceImpl.GenerateValuesFromDynamicExcel;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpAttachmentsValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpEduQualificationValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpEmergencyValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpFamilyValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpHealthValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpPersonalInfoValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpPreviousEmploymentValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpReportingOfficerValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmployeeValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.JobInfoValidator;
import com.sttl.hrms.bean.JsonResponse;
import com.sttl.hrms.bean.ListItems;
import com.sttl.hrms.leaveattendance.model.WeeklyOffPolicyMaster;
import com.sttl.hrms.leaveattendance.repository.WeeklyOffPolicyMasterRepository;
import com.sttl.hrms.login.repository.UserMasterRepository;
import com.sttl.hrms.masters.model.AjaxResponseBody;
import com.sttl.hrms.masters.model.BankBranchMaster;
import com.sttl.hrms.masters.model.BankMaster;
import com.sttl.hrms.masters.model.CastMaster;
import com.sttl.hrms.masters.model.CityMaster;
import com.sttl.hrms.masters.model.CountryMaster;
import com.sttl.hrms.masters.model.DepartmentMaster;
import com.sttl.hrms.masters.model.DesignationMaster;
import com.sttl.hrms.masters.model.DisabilityTypeMaster;
import com.sttl.hrms.masters.model.DistrictMaster;
import com.sttl.hrms.masters.model.EducationMaster;
import com.sttl.hrms.masters.model.FamilyRelationMaster;
import com.sttl.hrms.masters.model.GroupMaster;
import com.sttl.hrms.masters.model.NomineeTypeMaster;
import com.sttl.hrms.masters.model.PayBandMaster;
import com.sttl.hrms.masters.model.ReligionMaster;
import com.sttl.hrms.masters.model.RoleMaster;
import com.sttl.hrms.masters.model.StateMaster;
import com.sttl.hrms.masters.model.StatusMaster;
import com.sttl.hrms.masters.model.UserMaster;
import com.sttl.hrms.masters.model.VehicleTypeMaster;
import com.sttl.hrms.masters.repository.BankBranchMasterRepository;
import com.sttl.hrms.masters.repository.BankMasterRepository;
import com.sttl.hrms.masters.repository.CastMasterRepository;
import com.sttl.hrms.masters.repository.CityMasterRepository;
import com.sttl.hrms.masters.repository.CompanyBranchMasterRepository;
import com.sttl.hrms.masters.repository.CompanyMasterRepository;
import com.sttl.hrms.masters.repository.CountryMasterRepository;
import com.sttl.hrms.masters.repository.DepartmentMasterRepository;
import com.sttl.hrms.masters.repository.DesignationMasterRepository;
import com.sttl.hrms.masters.repository.DistrictMasterRepository;
import com.sttl.hrms.masters.repository.FamilyRelationMasterRepository;
import com.sttl.hrms.masters.repository.GroupMasterRepository;
import com.sttl.hrms.masters.repository.NomineeTypeMasterRepository;
import com.sttl.hrms.masters.repository.PayBandMasterRepository;
import com.sttl.hrms.masters.repository.PayCommissionMasterRepository;
import com.sttl.hrms.masters.repository.ReligionMasterRepository;
import com.sttl.hrms.masters.repository.RoleMasterRepository;
import com.sttl.hrms.masters.repository.ShiftPolicyMasterRepository;
import com.sttl.hrms.masters.repository.StateMasterRepositry;
import com.sttl.hrms.masters.repository.StatusMasterRepository;
import com.sttl.hrms.masters.repository.VehicleTypeMasterRepository;
import com.sttl.hrms.masters.service.DisabilityTypeMasterService;
import com.sttl.hrms.masters.service.EducationMasterService;
import com.sttl.hrms.masters.service.FamilyRelationMasterService;
import com.sttl.hrms.masters.service.GradeMasterService;
import com.sttl.hrms.masters.service.IrfclApproachService;
import com.sttl.hrms.masters.service.PayBandMasterService;
import com.sttl.hrms.masters.service.PayCommissionMasterService;
import com.sttl.hrms.masters.service.StatusMasterService;
import com.sttl.hrms.model.CompanyBranchMaster;
import com.sttl.hrms.model.CompanyMaster;
import com.sttl.hrms.model.FileMaster;
import com.sttl.hrms.model.HrmsCode;
import com.sttl.hrms.notification.service.AuditTrailService;
import com.sttl.hrms.notification.service.NotificationMasterService;
import com.sttl.hrms.repository.FileMasterRepository;
import com.sttl.hrms.security.util.SecurityChecker;
import com.sttl.hrms.service.HrmsCodeService;
import com.sttl.hrms.statemachine.workflow.data.enums.Pair;
import com.sttl.hrms.statemachine.workflow.data.enums.WorkflowType;
import com.sttl.hrms.statemachine.workflow.data.model.entity.EmployeeAppWorkFlowInstanceEntity;
import com.sttl.hrms.statemachine.workflow.data.model.entity.WorkflowInstanceEntity;
import com.sttl.hrms.statemachine.workflow.data.model.entity.WorkflowRoleMaster;
import com.sttl.hrms.statemachine.workflow.data.model.entity.WorkflowRuleConfigurationMaster;
import com.sttl.hrms.statemachine.workflow.data.model.repository.EmployeeAppWorkFlowInstanceRepository;
import com.sttl.hrms.statemachine.workflow.data.model.repository.WorkflowInstanceRepository;
import com.sttl.hrms.statemachine.workflow.data.model.repository.WorkflowRolesMasterRepository;
import com.sttl.hrms.statemachine.workflow.data.model.repository.WorkflowRuleConfiguratiionMasterRepository;
import com.sttl.hrms.statemachine.workflow.resource.dto.EmployeeAppWFInstanceDto;
import com.sttl.hrms.statemachine.workflow.resource.dto.EventResponseDto;
import com.sttl.hrms.statemachine.workflow.resource.dto.PassEventDto;
import com.sttl.hrms.statemachine.workflow.service.EmployeeAppService;
import com.sttl.hrms.statemachine.workflow.statemachine.builder.EventResultDto;
import com.sttl.hrms.util.ClientIpUtil;
import com.sttl.hrms.util.CommonConstant;
import com.sttl.hrms.util.CommonUtil;
import com.sttl.hrms.util.CommonUtility;
import com.sttl.hrms.util.DateUtil;
import com.sttl.hrms.util.ImageResize;
import com.sttl.hrms.util.JasperExportConstant;
import com.sttl.hrms.util.JasperExporter;
import com.sttl.hrms.util.JasperUtil;
import com.sttl.hrms.util.JrxmlConstant;
import com.sttl.hrms.util.NotificationAction;
import com.sttl.hrms.util.NotificationModule;
import com.sttl.hrms.util.PaginationUtil;
import com.sttl.hrms.util.SilverUtil;
import com.sttl.hrms.util.StringUtil;
import com.sttl.irfcl.payroll.model.CafeteriaApproachMaster;
import com.sttl.irfcl.payroll.model.ESIC;
import com.sttl.irfcl.payroll.model.ElementOfPaySystemMasterEntity;
import com.sttl.irfcl.payroll.model.EmpApproachDtl;
import com.sttl.irfcl.payroll.model.EmpApproachMst;
import com.sttl.irfcl.payroll.model.EmpSalaryGridDto;
import com.sttl.irfcl.payroll.model.FormulaCalculationBaseEntity;
import com.sttl.irfcl.payroll.model.PayEmpGroupMst;
import com.sttl.irfcl.payroll.model.PayStructDetail;
import com.sttl.irfcl.payroll.model.PayStructMaster;
import com.sttl.irfcl.payroll.model.PayStructTemplateDetail;
import com.sttl.irfcl.payroll.model.PayStructTemplateMaster;
import com.sttl.irfcl.payroll.model.PayUTEGISMaster;
import com.sttl.irfcl.payroll.model.ProvidentFund;
import com.sttl.irfcl.payroll.model.RoundMaster;
import com.sttl.irfcl.payroll.model.UTEGISMasterDtl;
//import com.sttl.hrms.excel.upload.dto.AddressExcelDto;
//import com.sttl.hrms.excel.upload.dto.ContactExcelDto;
//import com.sttl.hrms.excel.upload.dto.EducationQualificationExcelDto;
//import com.sttl.hrms.excel.upload.dto.EmergencyExcelDto;
//import com.sttl.hrms.excel.upload.dto.EmployeeExcelDto;
//import com.sttl.hrms.excel.upload.dto.FamilyExcelDto;
//import com.sttl.hrms.excel.upload.dto.HealthExcelDto;
//import com.sttl.hrms.excel.upload.dto.JobExcelDto;
//import com.sttl.hrms.excel.upload.dto.ManualAdhocExcelDto;
//import com.sttl.hrms.excel.upload.dto.NomineeExcelDto;
//import com.sttl.hrms.excel.upload.dto.PersonalExcelDto;
//import com.sttl.hrms.excel.upload.dto.PreviousEmployementExcelDto;
//import com.sttl.hrms.excel.upload.dto.ReportingOfficeExcelDto;
//import com.sttl.hrms.excel.upload.utils.DataType;
//import com.sttl.hrms.excel.upload.utils.ExcelColumnValidator;
//import com.sttl.hrms.excel.upload.utils.ExcelCommonConstant;
//import com.sttl.hrms.excel.upload.utils.ExcelUtils;
//import com.sttl.hrms.excel.upload.validator.ManualAdhocCustomValidator;
//import com.sttl.hrms.leaveattendance.controller.ExcelGenerator;
//import com.sttl.hrms.leaveattendance.model.LvAutoAttndDtl;
//import com.sttl.hrms.HR.empmgmt.empinfo.model.RecruitmentType;
//import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmployeeCategoryRepository;
//import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmployeementTypeRepository;
//import com.sttl.hrms.leaveattendance.repository.FinalCsvDataRepository;
//import com.sttl.hrms.masters.model.GradeMaster;
//import com.sttl.hrms.masters.model.GradePayMaster;
//import com.sttl.hrms.masters.model.PayCommissionMaster;
//import com.sttl.hrms.masters.model.ShiftPolicyMaster;
//import com.sttl.hrms.masters.repository.EducationMasterRepository;
//import com.sttl.hrms.masters.repository.GradePayMasterRepository;
//import com.sttl.hrms.masters.service.GradePayMasterService;
//import com.sttl.hrms.model.FinalCsvData;
//import org.apache.poi.ss.usermodel.CellType;
//import org.apache.poi.ss.usermodel.DataFormatter;
//import org.apache.poi.ss.usermodel.CellType;
//import org.apache.poi.ss.usermodel.DataFormatter;
//import java.util.Date;
//import org.apache.poi.xssf.usermodel.XSSFDataFormat;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
//import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpAddressDtl;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpAttachmentsDtl;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpContactDtl;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpEducationQualificationDtl;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpEmergencyDtl;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpFamilyDtl;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpHealthDtl;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpPersonalInfo;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpPhotoDtl;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpPreviousEmployment;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpReportingOfficer;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmpSalaryDtl;
import com.sttl.hrms.HR.empmgmt.empinfo.model.Employee;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmployeementCategory;
import com.sttl.hrms.HR.empmgmt.empinfo.model.EmployeementType;
import com.sttl.hrms.HR.empmgmt.empinfo.model.Job;
import com.sttl.hrms.HR.empmgmt.empinfo.model.Nominee;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpAddressRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpAttachmentsRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpContactRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpEducationQualificationDtlRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpEmergencyRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpFamilyRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpHealthRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpPersonalInfoRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpPhotoDtlRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpPreviousEmploymentRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpReportingOfficerRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmpSalaryDtlRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.EmployeeRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.JobRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.repository.NomineeRepository;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmpAddressDtlService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmpEducationQualificationDtlService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmpEmergencyService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmpFamilyService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmpReportingOfficerService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmployeeCategoryService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmployeeService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.EmployeementTypeService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.JobService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.NomineeService;
import com.sttl.hrms.HR.empmgmt.empinfo.service.RecruitmentTypeService;
import com.sttl.hrms.HR.empmgmt.empinfo.serviceImpl.GenerateValuesFromDynamicExcel;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpAttachmentsValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpEduQualificationValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpEmergencyValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpFamilyValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpHealthValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpPersonalInfoValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpPreviousEmploymentValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmpReportingOfficerValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.EmployeeValidator;
import com.sttl.hrms.HR.empmgmt.empinfo.validator.JobInfoValidator;
import com.sttl.hrms.bean.JsonResponse;
import com.sttl.hrms.bean.ListItems;
import com.sttl.hrms.empservicecycle.model.EmployeePromotion;
import com.sttl.hrms.excel.upload.dto.AddressExcelDto;
import com.sttl.hrms.excel.upload.dto.ContactExcelDto;
import com.sttl.hrms.excel.upload.dto.EducationQualificationExcelDto;
import com.sttl.hrms.excel.upload.dto.EmergencyExcelDto;
import com.sttl.hrms.excel.upload.dto.EmployeeExcelDto;
import com.sttl.hrms.excel.upload.dto.FamilyExcelDto;
import com.sttl.hrms.excel.upload.dto.HealthExcelDto;
import com.sttl.hrms.excel.upload.dto.JobExcelDto;
import com.sttl.hrms.excel.upload.dto.ManualAdhocExcelDto;
import com.sttl.hrms.excel.upload.dto.NomineeExcelDto;
import com.sttl.hrms.excel.upload.dto.PersonalExcelDto;
import com.sttl.hrms.excel.upload.dto.PreviousEmployementExcelDto;
import com.sttl.hrms.excel.upload.dto.ReportingOfficeExcelDto;
import com.sttl.hrms.excel.upload.utils.DataType;
import com.sttl.hrms.excel.upload.utils.ExcelColumnValidator;
import com.sttl.hrms.excel.upload.utils.ExcelCommonConstant;
import com.sttl.hrms.excel.upload.utils.ExcelUtils;
import com.sttl.hrms.excel.upload.validator.ManualAdhocCustomValidator;
import com.sttl.hrms.leaveattendance.controller.ExcelGenerator;
import com.sttl.hrms.leaveattendance.model.LvAutoAttndDtl;
import com.sttl.hrms.leaveattendance.model.WeeklyOffPolicyMaster;
import com.sttl.hrms.leaveattendance.repository.FinalCsvDataRepository;
import com.sttl.hrms.leaveattendance.repository.WeeklyOffPolicyMasterRepository;
import com.sttl.hrms.login.repository.UserMasterRepository;
import com.sttl.hrms.masters.model.AjaxResponseBody;
import com.sttl.hrms.masters.model.BankBranchMaster;
import com.sttl.hrms.masters.model.BankMaster;
import com.sttl.hrms.masters.model.CastMaster;
import com.sttl.hrms.masters.model.CityMaster;
import com.sttl.hrms.masters.model.CountryMaster;
import com.sttl.hrms.masters.model.DepartmentMaster;
import com.sttl.hrms.masters.model.DesignationMaster;
import com.sttl.hrms.masters.model.DisabilityTypeMaster;
import com.sttl.hrms.masters.model.DistrictMaster;
import com.sttl.hrms.masters.model.EducationMaster;
import com.sttl.hrms.masters.model.FamilyRelationMaster;
import com.sttl.hrms.masters.model.GradeMaster;
import com.sttl.hrms.masters.model.GradePayMaster;
import com.sttl.hrms.masters.model.GroupMaster;
import com.sttl.hrms.masters.model.NomineeTypeMaster;
import com.sttl.hrms.masters.model.PayBandMaster;
import com.sttl.hrms.masters.model.PayCommissionMaster;
import com.sttl.hrms.masters.model.ReligionMaster;
import com.sttl.hrms.masters.model.RoleMaster;
import com.sttl.hrms.masters.model.ShiftPolicyMaster;
import com.sttl.hrms.masters.model.StateMaster;
import com.sttl.hrms.masters.model.StatusMaster;
import com.sttl.hrms.masters.model.UserMaster;
import com.sttl.hrms.masters.model.VehicleTypeMaster;
import com.sttl.hrms.masters.repository.BankBranchMasterRepository;
import com.sttl.hrms.masters.repository.BankMasterRepository;
import com.sttl.hrms.masters.repository.CastMasterRepository;
import com.sttl.hrms.masters.repository.CityMasterRepository;
import com.sttl.hrms.masters.repository.CompanyBranchMasterRepository;
import com.sttl.hrms.masters.repository.CompanyMasterRepository;
import com.sttl.hrms.masters.repository.CountryMasterRepository;
import com.sttl.hrms.masters.repository.DepartmentMasterRepository;
import com.sttl.hrms.masters.repository.DesignationMasterRepository;
import com.sttl.hrms.masters.repository.DistrictMasterRepository;
import com.sttl.hrms.masters.repository.EducationMasterRepository;
import com.sttl.hrms.masters.repository.FamilyRelationMasterRepository;
import com.sttl.hrms.masters.repository.GradePayMasterRepository;
import com.sttl.hrms.masters.repository.GroupMasterRepository;
import com.sttl.hrms.masters.repository.NomineeTypeMasterRepository;
import com.sttl.hrms.masters.repository.PayBandMasterRepository;
import com.sttl.hrms.masters.repository.PayCommissionMasterRepository;
import com.sttl.hrms.masters.repository.ReligionMasterRepository;
import com.sttl.hrms.masters.repository.RoleMasterRepository;
import com.sttl.hrms.masters.repository.ShiftPolicyMasterRepository;
import com.sttl.hrms.masters.repository.StateMasterRepositry;
import com.sttl.hrms.masters.repository.StatusMasterRepository;
import com.sttl.hrms.masters.repository.VehicleTypeMasterRepository;
import com.sttl.hrms.masters.service.DisabilityTypeMasterService;
import com.sttl.hrms.masters.service.EducationMasterService;
import com.sttl.hrms.masters.service.FamilyRelationMasterService;
import com.sttl.hrms.masters.service.GradeMasterService;
import com.sttl.hrms.masters.service.GradePayMasterService;
import com.sttl.hrms.masters.service.IrfclApproachService;
import com.sttl.hrms.masters.service.PayBandMasterService;
import com.sttl.hrms.masters.service.PayCommissionMasterService;
import com.sttl.hrms.masters.service.StatusMasterService;
import com.sttl.hrms.model.CompanyBranchMaster;
import com.sttl.hrms.model.CompanyMaster;
import com.sttl.hrms.model.FileMaster;
import com.sttl.hrms.model.FinalCsvData;
import com.sttl.hrms.model.HrmsCode;
import com.sttl.hrms.notification.service.AuditTrailService;
import com.sttl.hrms.notification.service.NotificationMasterService;
import com.sttl.hrms.payroll.repository.CafeteriaConfigurationRepository;
import com.sttl.hrms.payroll.repository.DAConfigurationRepository;
import com.sttl.hrms.payroll.repository.DPConfigurationRepository;
import com.sttl.hrms.payroll.repository.ESICRepository;
import com.sttl.hrms.payroll.repository.ElementOfPaySystemMasterRepository;
import com.sttl.hrms.payroll.repository.EmpApproachDtlRepository;
import com.sttl.hrms.payroll.repository.EmpApproachMstRepository;
import com.sttl.hrms.payroll.repository.FormulaCreationRepository;
import com.sttl.hrms.payroll.repository.GISMasterDtlRepository;
import com.sttl.hrms.payroll.repository.PayEmpGroupMstRepository;
import com.sttl.hrms.payroll.repository.PayStructDetailRepository;
import com.sttl.hrms.payroll.repository.PayStructMasterRepository;
import com.sttl.hrms.payroll.repository.PayStructureDetailsRepository;
import com.sttl.hrms.payroll.repository.PayStructureMasterRepository;
import com.sttl.hrms.payroll.repository.PayUTEGISMasterRepository;
import com.sttl.hrms.payroll.repository.ProvidentFundRepository;
import com.sttl.hrms.payroll.repository.RoundMasterRepository;
import com.sttl.hrms.repository.FileMasterRepository;
import com.sttl.hrms.service.HrmsCodeService;
import com.sttl.hrms.util.CommonConstant;
import com.sttl.hrms.util.CommonUtil;
import com.sttl.hrms.util.CommonUtility;
import com.sttl.hrms.util.DateUtil;
import com.sttl.hrms.util.ImageResize;
import com.sttl.hrms.util.JasperExportConstant;
import com.sttl.hrms.util.JasperExporter;
import com.sttl.hrms.util.JasperUtil;
import com.sttl.hrms.util.JrxmlConstant;
import com.sttl.hrms.util.NotificationAction;
import com.sttl.hrms.util.NotificationModule;
import com.sttl.hrms.util.PaginationUtil;
import com.sttl.hrms.util.SilverUtil;
import com.sttl.hrms.util.StringUtil;
import com.sttl.irfcl.payroll.model.CafeteriaApproachMaster;
import com.sttl.irfcl.payroll.model.ESIC;
import com.sttl.irfcl.payroll.model.ElementOfPaySystemMasterEntity;
import com.sttl.irfcl.payroll.model.EmpApproachDtl;
import com.sttl.irfcl.payroll.model.EmpApproachMst;
import com.sttl.irfcl.payroll.model.EmpSalaryGridDto;
import com.sttl.irfcl.payroll.model.FormulaCalculationBaseEntity;
import com.sttl.irfcl.payroll.model.GISMasterDtl;
import com.sttl.irfcl.payroll.model.NPSConfiguration;
import com.sttl.irfcl.payroll.model.PayEmpGroupMst;
import com.sttl.irfcl.payroll.model.PayStructDetail;
import com.sttl.irfcl.payroll.model.PayStructMaster;
import com.sttl.irfcl.payroll.model.PayStructTemplateDetail;
import com.sttl.irfcl.payroll.model.PayStructTemplateMaster;
import com.sttl.irfcl.payroll.model.PayUTEGISMaster;
import com.sttl.irfcl.payroll.model.ProvidentFund;
import com.sttl.irfcl.payroll.model.RoundMaster;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.util.Date;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;


@Controller
@RequestMapping("/hrms/employee")
public class EmployeeController extends JasperUtil {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @Autowired
    private AuditTrailService auditTrailService;

    @Autowired
    private HrmsCodeService hrmsCodeService;

    @Autowired
    private EmpReportingOfficerRepositoryHistory empReportingOfficerRepositoryHistory;

    @Autowired
    private PayBandMasterRepository payBandMasterRepository;

    @Autowired
    private DepartmentMasterRepository departmentMasterRepository;

    @Autowired
    private GroupMasterRepository groupMasterRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompanyMasterRepository companyMasterRepository;

    @Autowired
    private CompanyBranchMasterRepository companyBranchMasterRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DesignationMasterRepository designationMasterRepository;

    @Autowired
    private ReligionMasterRepository religionMasterRepository;

    @Autowired
    private CastMasterRepository castMasterRepository;

    @Autowired
    private CountryMasterRepository countryMasterRepository;

    @Autowired
    private EmpFamilyRepository empFamilyRepository;

    @Autowired
    private EmpFamilyService empFamilyService;

    @Autowired
    private EmpHealthRepository empHealthRepository;

    @Autowired
    private DisabilityTypeMasterService disabilityTypeMasterService;

    @Autowired
    private EmpEducationQualificationDtlRepository educationQualificationDtlRepository;

    @Autowired
    private EmpEducationQualificationDtlService educationQualificationDtlRepositoryService;

    @Autowired
    private BankMasterRepository bankMasterRepository;

    @Autowired
    private BankBranchMasterRepository bankBranchMasterRepository;

    @Autowired
    private StateMasterRepositry stateMasterRepositry;

    @Autowired
    private EmpPersonalInfoRepository empPersonalInfoRepository;

    @Autowired
    private EmpAttachmentsRepository empAttachmentsRepository;

    @Autowired
    private StatusMasterService statusMasterService;

    @Autowired
    private GradeMasterService gradeService;

    @Autowired
    private PayCommissionMasterService paycommissionService;

    @Autowired
    private RecruitmentTypeService recruitmentTypeService;

    @Autowired
    private EmployeementTypeService empTypeService;

    @Autowired
    private EmployeeCategoryService empCatService;

    @Autowired
    private CompanyMasterRepository compRepo;

    @Autowired
    private EmpEmergencyDtlHistoryRepository empEmergencyDtlHistoryRepository;

    @Autowired
    private EmpPreviousEmploymentHistoryRepository empPreviousEmploymentHistoryRepository;
    @Autowired
    private JobService jobService;

    @Autowired
    private EmpContactRepository empContactRepository;

    @Autowired
    private EmpEmergencyRepository empEmergencyRepository;

    @Autowired
    private EmpPreviousEmploymentRepository empPreviousEmploymentRepository;

    @Autowired
    private EmpAddressRepository empAddressRepository;

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private PayBandMasterService payBandService;

    @Autowired
    private ShiftPolicyMasterRepository shiftPolicyMasterRepo;

    @Autowired
    private FamilyRelationMasterService familyRelationMasterService;

    @Autowired
    private EducationMasterService educationMasterService;

//	@Autowired
//	private GradeMasterService gradeMasterService;


    @Autowired
    private EmpFamilyDtlHistoryRepository empFamilyDtlHistoryRepository;

    @Autowired
    private EmpAddressDtlService empAddressDtlService;

    @Autowired
    private DistrictMasterRepository districtMasterRepository;

    @Autowired
    private CityMasterRepository cityMasterRepository;

    @Autowired
    private EmpReportingOfficerRepository empReportingOfficerRepository;

    @Autowired
    private EmpReportingOfficerService empReportingOfficerService;

    @Autowired
    private RoleMasterRepository roleMasterRepository;

    @Autowired
    private CommonUtility commonUtility;

    @Autowired
    private EmpPhotoDtlRepository empPhotoDtlRepository;

    @Autowired
    private Environment environment;

//	@Autowired
//	private NomineeTypeMasterService nomineeTypeMasterService;

    @Autowired
    private NomineeTypeMasterRepository nomineeTypeMasterRepo;

    @Autowired
    private NomineeService nomineeService;

    @Autowired
    private NomineeRepository nomineeRepo;

    @Autowired
    private ImageResize imageResize;

    @Autowired
    private WeeklyOffPolicyMasterRepository weeklyOffPolicyMaster;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private FileMasterRepository fileMasterRepository;

    @Autowired
    private EmpEmergencyService empEmergencyService;

    @Autowired
    RoundMasterRepository roundMasterRepo;

    @Autowired
    private ElementOfPaySystemMasterRepository elementOfPaySys;

    // Parth changed

    @Autowired
    private GISMasterRepository gisMasterRepository;


    @Autowired
    private PayUTEGISMasterRepository payutegismasterRepo;

    @Autowired
    private DAConfigurationRepository daConfigurationRepository;

    @Autowired
    private PayEmpGroupMstRepository payEmpGroupMstRepo;

    @Autowired
    private PayStructMasterRepository paystructTemplateMstRepo;

    @Autowired
    private PayStructDetailRepository payStructDetailRepository;

    @Autowired
    private EmpSalaryDtlRepository salaryDtlRepo;

    @Autowired
    private PayStructureMasterRepository payStructureMasterRepository;

//	@Autowired
//	private PayStructDetailRepository payStructDetailsRepo;

    @Autowired
    private PayStructureDetailsRepository payStructureDetailsRepository;

    @Autowired
    private EmpSalaryDtlRepository empSalaryDtlRepository;

    @Autowired
    private GenerateValuesFromDynamicExcel testNames;

    @Autowired
    private FormulaCreationRepository formulaCreationRepository;

    @Autowired
    private ProvidentFundRepository providentFundRepository;

    @Autowired
    private ESICRepository esicRepository;

//	@Autowired
//	private BonusRepository bonusRepository;
//
//	@Autowired
//	private GratuityRepository gratuityRepository;

    @Autowired
    private DPConfigurationRepository dpConfigurationRepository;

    @Autowired
    private IrfclApproachService irfclApproachService;

    @Autowired
    private ElementOfPaySystemMasterRepository elementOfPaySystemMasterRepository;

    @Autowired
    private CafeteriaConfigurationRepository cafeteriaConfigurationRepository;

//	@Autowired
//	private DAConfigurationRepository daConfigurationRepository2;

    @Autowired
    private GISMasterDtlRepository gisDtlRepository;

    @Autowired
    private EmpApproachMstRepository empApproachMstRepository;

    @Autowired
    private PayCommissionMasterRepository payCommissionMasterRepo;

    @Autowired
    EntityManager entityManager;

    @Autowired
    JasperExporter jasperExporter;

    @Autowired
    private EmpApproachDtlRepository empApproachDtlRepository;

    @Autowired
    private NotificationMasterService notificationMasterService;

    @Autowired
    private VehicleTypeMasterRepository vehicleTypeMasterRepository;

    @Autowired
    private FamilyRelationMasterRepository familyRelationMasterRepository;

    @Autowired
    private StatusMasterRepository statusMasterRepository;

    @Autowired
    private WorkflowRuleConfiguratiionMasterRepository configuratiionMasterRepository;

    @Autowired
    SecurityChecker securityChecker;

    @Autowired
    private EmployeeAppService employeeAppService;

    @Autowired
    private EmpReportingOfficerRepository reportingOfficerRepository;

    @Autowired
    private WorkflowRolesMasterRepository workflowRolesMasterRepository;

    @Autowired
    private EmpPersonalInfoTempRepository empPersonalInfoTempRepository;

    @Autowired
    private EmpHealthTempRespository empHealthTempRepository;

    @Autowired
    private EmpPhotoDtlTempRepository empPhotoDtlTempRepository;

    @Autowired
    private EmpContactDtlTempRepository empContactDtlTempRepository;

    @Autowired
    private EmpAddressDtlTempRepository empAddressDtlTempRepository;

    @Autowired
    private EmpAddressDtlHistoryRepository empAddressDtlHistoryRepository;

    @Autowired
    private EmpFamilyDtlTempRepository empFamilyDtlTempRepository;

    @Autowired
    private EmpEmergencyDtlTempRepository empEmergencyDtlTempRepository;

    @Autowired
    private EmpEducationQualificationDtlTempRepository empEducationQualificationDtlTempRepository;

    @Autowired
    private EmpEducationQualificationDtlHistoryRepository empEducationQualificationDtlHistoryRepository;

    @Autowired
    private EmpAttachmentsTempRepository empAttachmentsTempRepository;

    @Autowired
    private NomineeTempRepository nomineeTempRepository;

    @Autowired
    private NomineeHistoryRepository nomineeHistoryRepository;

    @Autowired
    private EmpPreviousEmploymentTempRepository empPreviousEmploymentTempRepository;

    @Autowired
    private UTEGISMasterDtlRepository utegisDtlRepository;

    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Autowired
    private EmpEducationQualificationDtlRepository empEducationQualificationDtlRepository;

    @Autowired
    private EmployeeCategoryRepository employeeCategoryRepository;

    @Autowired
    private EmployeementTypeRepository employeementTypeRepository;

    @Autowired
    private EducationMasterRepository educationMasterRepository;

    @Autowired
    private GradePayMasterRepository gradePayMasterRepository;

    @Autowired
    private FinalCsvDataRepository finalCsvDataRepository;

    @Autowired
    private NPSConfigurationRepository npsConfigurationRepository;

    @Autowired
    private EmployeeAppWorkFlowInstanceRepository employeeAppWorkFlowInstanceRepository;

    @GetMapping(value = "/test")
    public String test(HttpSession session, Model mv) {

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            return "hrms/login";
        } else {

            List<Employee> employeeList = employeeRepository
                    .findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(companyId,
                            companyBranchId);
            mv.addAttribute("employeeList", employeeList);

            List<HrmsCode> monthList = hrmsCodeService.findByFieldName("MONTHS");
            mv.addAttribute("monthList", monthList);
        }

        return "hrms/HR/empmgmt/empinfo/iframe";
    }

    @GetMapping(value = "/test2")
    public String test2(HttpSession session, Model mv) {
        // System.err.println("session::" + session.getAttribute("userRoleId"));
        return "hrms/HR/empmgmt/empinfo/employee";
    }

    @RequestMapping(value = "/employeeList")
    public String employeeList(@RequestParam(value = "rowItems", defaultValue = "10") int rowItems,
                               @RequestParam(value = "ipageId", defaultValue = "0") int ipageId,
                               @RequestParam(value = "opageId", defaultValue = "0") int opageId, Model model, HttpServletRequest request,
                               HttpSession session) {
        logger.info("EmployeeController.employeeList()");

        // int rowItems = 10, opageId = 1, start = 0, end = 0;

        try {

            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");
            Long roleId = (Long) session.getAttribute("roleId");
            System.out.println("um.getRoleMasterId().getId() " + um.getRoleMasterId().getId());
            if (um == null || companyId == null || companyBranchId == null) {
                return "hrms/login";
            }

            List<ListItems> actionList = SilverUtil.getBulkActionList();
            model.addAttribute("actionList", actionList);
            Pageable pageable = Pageable.unpaged();
            int start = 0;
            int end = 0;
//			Pageable pageable = PageRequest.of(ipageId, rowItems, Sort.by("id").descending());
//			if (opageId == 0) {
//				pageable = PageRequest.of(0, rowItems, Sort.by(Sort.Direction.DESC, "id"));
//				start = 1;
//				end = rowItems;
//			} else {
//				pageable = PageRequest.of(opageId - 1, rowItems, Sort.by(Sort.Direction.DESC, "id"));
//				start = (rowItems * (opageId - 1) + 1);
//				end = rowItems * opageId;
//			}

//			if (request.getParameter("rowItems") != null) {
//				rowItems = Integer.parseInt(request.getParameter("rowItems"));
//			}
//
//			if (request.getParameter("opageId") != null) {
//				opageId = Integer.parseInt(request.getParameter("opageId"));
//			}

            // PaginationUtil.pgInfo(model, opageId, start, end, rowItems);

//			Pageable pageable = PageRequest.of((opageId - 1), rowItems);
            Page<Employee> employeeList = null;
            Map<String, Object> dataMap = new HashMap<>();

            RoleMaster role = roleMasterRepository.findByIdAndIsDelete(roleId,false);

            if (role.getRoleName().equals(CommonConstant.SUPER_ADMIN)
                    || role.getRoleName().equals(CommonConstant.ADMINISTRATOR) || role.getRoleName().equals(CommonConstant.IRFCL_ADMIN_ROLE)) {
                dataMap.put("COM_ID", companyId);
                dataMap.put("COM_BR_ID", companyBranchId);
                dataMap.put("PAGEABLE", pageable);
                employeeList = employeeRepository.findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchId(companyId,companyBranchId,pageable);
            }else if (role.getRoleName().equals(CommonConstant.EMP_ROLE)) {
                dataMap.put("EMP_ID", um.getEmpId().getId());
                dataMap.put("COM_ID", companyId);
                dataMap.put("COM_BR_ID", companyBranchId);
                dataMap.put("PAGEABLE", pageable);
                model.addAttribute("role", um.getRoleMasterId().getRoleName());

                employeeList = employeeRepository.findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdAndId(companyId,companyBranchId,um.getEmpId().getId(),pageable);

            } else {
                dataMap.put("COM_ID", companyId);
                dataMap.put("COM_BR_ID", companyBranchId);
                dataMap.put("PAGEABLE", pageable);
                employeeList = employeeRepository.findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchId(companyId,companyBranchId,pageable);
            }

//			Map<String, Object> resultMap = employeeService.findAll(dataMap);
//
//			@SuppressWarnings("unchecked")
//			Page<Employee> employeeList = (Page<Employee>) resultMap.get("PAGABLE_LIST");

            if (employeeList != null && employeeList.getContent().size() > 0) {
                Long size = employeeList.getTotalElements();
                // rowItems = size.intValue();

                employeeList.getContent().forEach(e -> {

                    EmpPhotoDtl empPhoto = empPhotoDtlRepository.findByEmpId(e.getEmpId());
                    if (empPhoto != null && empPhoto.getProfileImg() != null) {
                        File file = new File(environment.getProperty("file.repository.hrms.path") + companyId
                                + File.separator + companyBranchId + File.separator + CommonConstant.EMP_FILES
                                + File.separator + empPhoto.getProfileImg().getFileName());
                        if (file != null && file.exists()) {
                            byte[] byteArry;
                            try {
                                byteArry = CommonUtility.toByteArray(file);
                                String userProfileImg = javax.xml.bind.DatatypeConverter.printBase64Binary(byteArry);
                                // empPhoto.setSignImgFile(userProfileImg);
                                e.setEmpProfileImg(userProfileImg);
                                // System.err.println("HI----"+e.getEmpProfileImg());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

                model.addAttribute("employeeList", employeeList.getContent());
            }

            model.addAttribute("listSizeDropDown", PaginationUtil.getShowPageList());
            model.addAttribute("oTotalPages", employeeList.getTotalPages());
            model.addAttribute("oTotalElements", employeeList.getTotalElements());
            model.addAttribute("rowItems", rowItems);
            model.addAttribute("opageId", opageId);
            model.addAttribute("ostartPage", (opageId == 0) ? 1 : opageId);
            model.addAttribute("oStart", start);
            model.addAttribute("oEnd", end);
            model.addAttribute("listSizeDropDown", PaginationUtil.getShowPageList());

            request = commonUtil.setMenuPermissionsInRequest(request, CommonConstant.EMPLOYEE_MASTER);

            model.addAttribute("isAdd", request.getAttribute("addPermission"));
            model.addAttribute("isEdit", request.getAttribute("editPermission"));
            model.addAttribute("isView", request.getAttribute("viewPermission"));
            model.addAttribute("isDelete", request.getAttribute("deletePermission"));
            auditTrailService.saveAuditTrailData("Employee", "ListPage", "Admin", NotificationModule.EMPLOYEE_INFO,
                    NotificationAction.LIST, "/employeeList", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "hrms/HR/empmgmt/empinfo/employeeList";
    }


    @RequestMapping(value = "/getemployeeList")
    public String getemployeeList(@RequestParam(value = "rowItems", defaultValue = "10") int rowItems,
                                  @RequestParam(value = "opageId", defaultValue = "0") int opageId, Model model, HttpServletRequest request,
                                  HttpSession session) {
        logger.info("EmployeeController.getemployeeList()");
        AjaxResponseBody result = new AjaxResponseBody();

        int start = 0, end = 0;

        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");
            Long roleId = (Long) session.getAttribute("roleId");

            if (um == null || companyId == null || companyBranchId == null) {
                return "hrms/login";
            }

            List<ListItems> actionList = SilverUtil.getBulkActionList();
            model.addAttribute("actionList", actionList);

            Pageable pageable = PageRequest.of(opageId, rowItems, Sort.by("id").descending());
            if (opageId == 0) {
                pageable = PageRequest.of(0, rowItems, Sort.by(Sort.Direction.DESC, "id"));
                start = 1;
                end = rowItems;
            } else {
                pageable = PageRequest.of(opageId - 1, rowItems, Sort.by(Sort.Direction.DESC, "id"));
                start = (rowItems * (opageId - 1) + 1);
                end = rowItems * opageId;
            }

            String empCodeSearch = request.getParameter("empCodeSearch");

            RoleMaster role = roleMasterRepository.findByIdAndIsDelete(roleId,false);



            Map<String, Object> dataMap = new HashMap<>();

            if (role.getRoleName().equals(CommonConstant.SUPER_ADMIN)
                    || role.getRoleName().equals(CommonConstant.ADMINISTRATOR) || role.getRoleName().equals(CommonConstant.IRFCL_ADMIN_ROLE)) {
                dataMap.put("COM_ID", companyId);
                dataMap.put("COM_BR_ID", companyBranchId);
                dataMap.put("EMP_CODE", empCodeSearch);
                dataMap.put("PAGEABLE", pageable);
            }else if (role.getRoleName().equals(CommonConstant.EMP_ROLE)) {
                dataMap.put("EMP_ID", um.getEmpId().getId());
                dataMap.put("COM_ID", companyId);
                dataMap.put("COM_BR_ID", companyBranchId);
                dataMap.put("EMP_CODE", empCodeSearch);
                dataMap.put("PAGEABLE", pageable);
                model.addAttribute("role", um.getRoleMasterId().getRoleName());
            } else {
                dataMap.put("COM_ID", companyId);
                dataMap.put("COM_BR_ID", companyBranchId);
                dataMap.put("EMP_CODE", empCodeSearch);
                dataMap.put("PAGEABLE", pageable);
            }

            Map<String, Object> resultMap = employeeService.findAll(dataMap);

            @SuppressWarnings("unchecked")
            Page<Employee> employeeList = (Page<Employee>) resultMap.get("PAGABLE_LIST");

            if (employeeList != null && employeeList.getContent().size() > 0) {
                Long size = employeeList.getTotalElements();
                rowItems = size.intValue();

                // employeeList.getContent().forEach(e -> {
                for (Employee e : employeeList) {
                    // System.err.println("id "+e.getId());
                    EmpPhotoDtl empPhoto = empPhotoDtlRepository.findByEmpId(e.getId());

                    if (empPhoto != null && empPhoto.getProfileImg() != null) {
                        File file = new File(environment.getProperty("file.repository.hrms.path") + companyId
                                + File.separator + companyBranchId + File.separator + CommonConstant.EMP_FILES
                                + File.separator + empPhoto.getProfileImg().getFileNameList());
                        if (file != null && file.exists()) {
                            byte[] byteArry;
                            try {
                                byteArry = CommonUtility.toByteArray(file);
                                String userProfileImg = javax.xml.bind.DatatypeConverter.printBase64Binary(byteArry);
                                // empPhoto.setSignImgFile(userProfileImg);
                                e.setEmpProfileImg(userProfileImg);
                                // System.err.println("BYE----"+e.getEmpProfileImg());

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    // });
                }

            }
            model.addAttribute("listSizeDropDown", PaginationUtil.getShowPageList());
            model.addAttribute("oTotalPages", employeeList.getTotalPages());
            model.addAttribute("oTotalElements", employeeList.getTotalElements());
            model.addAttribute("rowItems", rowItems);
            model.addAttribute("opageId", opageId);
            model.addAttribute("ostartPage", (opageId == 0) ? 1 : opageId);
            model.addAttribute("oStart", start);
            model.addAttribute("oEnd", end);
            model.addAttribute("listSizeDropDown", PaginationUtil.getShowPageList());
            model.addAttribute("employeeList", employeeList.getContent());
            request = commonUtil.setMenuPermissionsInRequest(request, CommonConstant.EMPLOYEE_MASTER);

            model.addAttribute("isAdd", request.getAttribute("addPermission"));
            model.addAttribute("isEdit", request.getAttribute("editPermission"));
            model.addAttribute("isView", request.getAttribute("viewPermission"));
            model.addAttribute("isDelete", request.getAttribute("deletePermission"));
            auditTrailService.saveAuditTrailData("Employee", "ListPage", "Admin", NotificationModule.EMPLOYEE_INFO,
                    NotificationAction.LIST, "/employeeList", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "hrms/HR/empmgmt/empinfo/employeeList";
    }


    @RequestMapping("/addEmployee")
    public String addEmployee(Model model, HttpSession session, HttpServletRequest request) {
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");
            Optional<CompanyMaster> cm = null;
            if (um == null || companyId == null || companyBranchId == null) {
                return "hrms/login";
            } else {
                // added this to get company name
                cm = companyMasterRepository.findById(companyId);

                model.addAttribute("companySName", cm.get().getCompanyName());

                model.addAttribute("companyCode", cm.get().getCompanyCodePrefix());

                if (um.getRoleMasterId().getIsAdmin()) {
                    model.addAttribute("isAdmin", true);
                } else {
                    model.addAttribute("isAdmin", false);
                }
//				int errorCode = employeeRepository.errorcode();
//				System.err.println("ErrorCode is "+errorCode);

//				String message = employeeRepository.message();
//				System.err.println("message is "+message);

                // emp info
                List<HrmsCode> salutationList = hrmsCodeService.findByFieldName("SALUTATION");
                model.addAttribute("salutationList", salutationList);

                List<HrmsCode> vehiLicenseList = hrmsCodeService.findByFieldName("VEHI_LIC_TYPE");
                model.addAttribute("vehiLicenseList", vehiLicenseList);

                List<HrmsCode> unitList = hrmsCodeService.findByFieldName("UNIT_TYPE");
                model.addAttribute("unitList", unitList);

                List<GroupMaster> groupMstList = groupMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("groupMstList", groupMstList);

                List<DepartmentMaster> deptMstList = departmentMasterRepository
                        .findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(companyId,
                                companyBranchId);
                model.addAttribute("deptMstList", deptMstList);

                List<DesignationMaster> designationMstList = designationMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("designationMstList", designationMstList);

                // personal info
                List<HrmsCode> genderList = hrmsCodeService.findByFieldName("GENDER");
                model.addAttribute("genderList", genderList);

                List<HrmsCode> maritalStatusList = hrmsCodeService.findByFieldName("MARITAL");
                model.addAttribute("maritalStatusList", maritalStatusList);

                List<HrmsCode> communityList = hrmsCodeService.findByFieldName("COMMUNITY");
                model.addAttribute("communityList", communityList);

                List<ReligionMaster> religionMstList = religionMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("religionMstList", religionMstList);

                List<CastMaster> castMstList = castMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("castMstList", castMstList);

                List<CountryMaster> countryMstList = countryMasterRepository.findAllByIsDeleteFalseOrderByCountryName();
                model.addAttribute("countryMstList", countryMstList);

                List<BankMaster> bankMstList = bankMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("bankMstList", bankMstList);

//				List<BankBranchMaster> bankBranchMstList = bankBranchMasterRepository.findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc
//						(companyId,companyBranchId);
//				model.addAttribute("bankBranchMstList", bankBranchMstList);

//				List<HrmsCode> licenseTypeList = hrmsCodeService.findByFieldName("VEHI_LIC_TYPE");
//				model.addAttribute("licenseTypeList", licenseTypeList);

                List<VehicleTypeMaster> licenseTypeList = vehicleTypeMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("licenseTypeList", licenseTypeList);

                List<EmployeementType> employeementTypeList = empTypeService.findALlByIsDeleteFalse();
                model.addAttribute("employeementTypeList", employeementTypeList);

                List<StateMaster> stateMstList = stateMasterRepositry
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE);
                model.addAttribute("stateMstList", stateMstList);

                List<StateMaster> stateList = stateMasterRepositry.findByCountry(250L);
                model.addAttribute("stateList", stateList);

                // familyInfo
                List<CountryMaster> citizenList = countryMasterRepository.findAllByIsDeleteFalseOrderByCitizenship();
                model.addAttribute("citizenList", citizenList);

                // healthinfo
                List<HrmsCode> bloodGroupList = hrmsCodeService.findByFieldName("BLD_GRP");
                model.addAttribute("bloodGroupList", bloodGroupList);

                List<DisabilityTypeMaster> disabilityTypeList = disabilityTypeMasterService
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("disabilityTypeList", disabilityTypeList);

                // job
                model.addAttribute("statusList", statusMasterService.findAll());

                model.addAttribute("gradeList", gradeService
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId));
                model.addAttribute("paycommissionList", payCommissionMasterRepo
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId));
                List<PayBandMaster> payBandMasters = payBandMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("payBandmasterList", payBandMasters);
                model.addAttribute("recruitmentTypeList", recruitmentTypeService.findAllByIsDeleteFalse());
                model.addAttribute("companyMasterList", compRepo.findAllByIsDeleteFalseOrderByCompanyName());

                // address info
                List<HrmsCode> addrTypeList = hrmsCodeService.findByFieldName("ADDRESS_TYPE");
                model.addAttribute("addrTypeList", addrTypeList);

                List<CountryMaster> countryList = countryMasterRepository.findAllByIsDeleteFalseOrderByCountryName();
                model.addAttribute("countryList", countryList);
                model.addAttribute("isAdd", true);

                model.addAttribute("shiftPolicyMasterList", shiftPolicyMasterRepo
                        .findAllByCompanyIdAndCompanyBranchIdAndIsDeleteFalse(companyId, companyBranchId));

                // Allowance Diclaration Approach Details

                model.addAttribute("irfclApproachList", irfclApproachService.findAll(companyId));
                model.addAttribute("fixedApproachList",
                        elementOfPaySystemMasterRepository.findAllByFixedElement(companyId));
                /*
                 * model.addAttribute("cafeteriaApproachList",
                 * cafeteriaConfigurationRepository.findAllAndIsDeleteFalse());
                 * model.addAttribute("employeeId", um.getEmpId());
                 */

                // Attachments
                List<HrmsCode> documentList = hrmsCodeService.findByFieldName("ATTACHMENT_TYPE");
                model.addAttribute("documentList", documentList);
                List<NomineeTypeMaster> nomineeTypeMasterList = nomineeTypeMasterRepo
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("nomineeTypeMasterList", nomineeTypeMasterList);

                // Emergency info
                List<FamilyRelationMaster> familyRelationList = familyRelationMasterService
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByDisplayOrderAsc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("familyRelationList", familyRelationList);

                // previous employment
                List<HrmsCode> serviceList = hrmsCodeService.findByFieldName("SERVICE_TYPE");
                model.addAttribute("serviceList", serviceList);

                CompanyMaster companyMasterCodeList = compRepo.findById(companyId).orElse(null);
                model.addAttribute("companyMasterCodeList", companyMasterCodeList);
            }
            // Education Qualification info
            List<EducationMaster> educationQualificationList = educationMasterService
                    .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByDisplaySortOrderAsc(
                            CommonConstant.ACTIVE, companyId, companyBranchId);
            model.addAttribute("educationQualificationList", educationQualificationList);

            List<HrmsCode> gradeList = hrmsCodeService.findByFieldName("GRADE");
            model.addAttribute("gradeHrmsList", gradeList);

            List<HrmsCode> modeOfStudyList = hrmsCodeService.findByFieldName("STUDY_MODE");
            model.addAttribute("modeOfStudyList", modeOfStudyList);

            String empCodePrefix = "0";
            String newEmployeeCode = employeeService.findMaxEmployeeCodeByPrefix(companyId, empCodePrefix);
            model.addAttribute("tempEmpCode", newEmployeeCode);

            model.addAttribute("employeeObj", new Employee());
            model.addAttribute("employeePersonalObj", new EmpPersonalInfo());
            model.addAttribute("jobObj", new Job());
            model.addAttribute("employeeSalaryObj", new EmpSalaryDtl());
            model.addAttribute("employeeContactObj", new EmpContactDtl());
            model.addAttribute("employeeEmergencyObj", new EmpEmergencyDtl());
            model.addAttribute("employeeAddressObj", new EmpAddressDtl());
            model.addAttribute("familyMasterObj", new EmpFamilyDtl());
            model.addAttribute("reportingOfficerObj", new EmpReportingOfficer());
            model.addAttribute("previousEmploymentObj", new EmpPreviousEmployment());
            model.addAttribute("employeeHealthObj", new EmpHealthDtl());
            model.addAttribute("educationQualificationObj", new EmpEducationQualificationDtl());
            model.addAttribute("employeeProfileObj", new EmpPhotoDtl());
            model.addAttribute("attachmentsObj", new EmpAttachmentsDtl());
            model.addAttribute("nomineeObj", new Nominee());
            model.addAttribute("empApproachMaster", new EmpApproachMst());
            auditTrailService.saveAuditTrailData("Employee", "AddPage", "Admin", NotificationModule.EMPLOYEE_INFO,
                    NotificationAction.ADD, "/addEmployee", userId);

            List<Employee> employees = employeeRepository
                    .findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchId(companyId, companyBranchId);
            System.err.println(employees.size());
            model.addAttribute("employees", employees);

            List<RoundMaster> roundList = roundMasterRepo.findAll();
            model.addAttribute("roundList", roundList);

//			model.addAttribute("peElementTypeList", elementOfPaySys.findAllByActiveTrueOrderByIdDesc());

            List<ElementOfPaySystemMasterEntity> peElementTypeList = elementOfPaySys
                    .findAllIsDeleteFalseByCompanyAndCompanyBranchAndElementType(companyId, companyBranchId);
            model.addAttribute("peElementTypeList", peElementTypeList);

            model.addAttribute("payUtegisMaster",
                    payutegismasterRepo.findTop2ByCompanyAndCompanyBranchAndIsDeleteFalseOrderByEffectiveDateDesc(
                            companyMasterRepository.findById(companyId).orElse(null),
                            companyBranchMasterRepository.findById(companyBranchId).orElse(null)));

            model.addAttribute("codes", hrmsCodeService.findByFieldName("HRAAdmissibility"));

            List<HrmsCode> daTypes = hrmsCodeService.findByFieldName("DA_TYPE");
            model.addAttribute("daTypes", daTypes);

//			model.addAttribute("das",
//					daConfigurationRepository.findAllByCompanyAndCompanyBranchAndIsDeleteFalse(
//							companyMasterRepository.findById(companyId).orElse(null),
//							companyBranchMasterRepository.findById(companyBranchId).orElse(null)));

            model.addAttribute("EEFcodes", hrmsCodeService.findByFieldName("EMPElgibleFor"));

            model.addAttribute("pf", providentFundRepository.findByLastRecord(companyId, companyBranchId));

            List<HrmsCode> monthList = hrmsCodeService.findByFieldName("MONTHS");

            System.out.println("monthList =========> " + monthList);
            model.addAttribute("monthList", monthList);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in add employee " + e.getMessage());
        }
        return "hrms/HR/empmgmt/empinfo/addEmployee";
    }

    @RequestMapping("/viewEmployee/{id}")
    public String viewEmployee(@PathVariable(value = "id") Long id, Model model, HttpSession session,
                               HttpServletRequest request) {
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                return "hrms/login";
            } else {
                request = commonUtil.setMenuPermissionsInRequest(request, CommonConstant.EMPLOYEE_MASTER);
                model.addAttribute("isEdit", request.getAttribute("editPermission"));
                model.addAttribute("isLogin", true);
                Optional<Employee> em = employeeRepository.findById(id);

                model.addAttribute("InstanceByEmpid", employeeAppWorkFlowInstanceRepository.findbyEmpId(id));


                if (em.isPresent()) {
                    HrmsCode hrmsCodeUnit = hrmsCodeService.findByFieldNameAndCode("UNIT_TYPE", em.get().getUnit());
                    if (hrmsCodeUnit != null) {
                        em.get().setUnitDesc(hrmsCodeUnit.getDescription());
                    }

                    if (em.get() != null && em.get().getIsGazeted() != null) {
                        Boolean isGazeted = em.get().getIsGazeted();
                        if (isGazeted) {
                            em.get().setIsGazetedStr("Yes");
                        } else {
                            em.get().setIsGazetedStr("No");
                        }
                    }

                    EmpPhotoDtl empPhoto = empPhotoDtlRepository.findByEmpId(id);

                    if (empPhoto != null && empPhoto.getProfileImg() != null) {
                        File file = new File(environment.getProperty("file.repository.hrms.path") + companyId
                                + File.separator + companyBranchId + File.separator + CommonConstant.EMP_FILES
                                + File.separator + empPhoto.getProfileImg().getFileNameView());
                        if (file != null && file.exists()) {
                            byte[] byteArry = CommonUtility.toByteArray(file);
                            String userProfileImg = javax.xml.bind.DatatypeConverter.printBase64Binary(byteArry);
                            em.get().setEmpProfileImg(userProfileImg);
                        }
                    }

                    model.addAttribute("employeeObj", em.get());
                }

                List<EmpPersonalInfo> empPersonalInfo1 = empPersonalInfoRepository.findByEmpIdOrderByIdDesc(id);
                EmpPersonalInfo empPersonalInfo = null;
                if (empPersonalInfo1 != null && empPersonalInfo1.size() > 0)
                    empPersonalInfo = empPersonalInfo1.get(0);
                if (empPersonalInfo != null) {

                    HrmsCode hrmsCodeGender = hrmsCodeService.findByFieldNameAndCode("GENDER",
                            empPersonalInfo.getGender());
                    if (hrmsCodeGender != null) {
                        empPersonalInfo.setGenderDesc(hrmsCodeGender.getDescription());
                    }

                    HrmsCode hrmsCodeMarriageStatus = hrmsCodeService.findByFieldNameAndCode("MARITAL",
                            empPersonalInfo.getMarriageStatus());
                    if (hrmsCodeMarriageStatus != null) {
                        empPersonalInfo.setMarriageStatusStr(hrmsCodeMarriageStatus.getDescription());
                    }

                    HrmsCode hrmsCodeCommnity = hrmsCodeService.findByFieldNameAndCode("COMMUNITY",
                            empPersonalInfo.getCategory());
                    if (hrmsCodeCommnity != null) {
                        empPersonalInfo.setCategoryStr(hrmsCodeCommnity.getDescription());
                    }

                    HrmsCode hrmsCodeliceType = hrmsCodeService.findByFieldNameAndCode("VEHI_LIC_TYPE",
                            empPersonalInfo.getLicenseIssuedFor());
                    if (hrmsCodeliceType != null) {
                        empPersonalInfo.setLicenseIssuedForStr(hrmsCodeliceType.getDescription());
                    }

                    String govtVehicle = empPersonalInfo.getGovtVehicle();
                    if (StringUtil.isNotEmpty(govtVehicle) && govtVehicle.equalsIgnoreCase("Y")) {
                        empPersonalInfo.setGovtVehicleStr("Yes");
                    } else {
                        empPersonalInfo.setGovtVehicleStr("No");
                    }

                    String isResidentOtherCountry = empPersonalInfo.getIsResidentOtherCountry();
                    if (StringUtil.isNotEmpty(isResidentOtherCountry) && isResidentOtherCountry.equalsIgnoreCase("Y")) {
                        empPersonalInfo.setIsResidentOtherCountryStr("Yes");
                    } else {
                        empPersonalInfo.setIsResidentOtherCountryStr("No");
                    }

                    String isAnyDisciplinaryProceding = empPersonalInfo.getIsAnyDisciplinaryProceding();
                    if (StringUtil.isNotEmpty(isAnyDisciplinaryProceding)
                            && isAnyDisciplinaryProceding.equalsIgnoreCase("Y")) {
                        empPersonalInfo.setIsAnyDisciplinaryProcedingStr("Yes");
                    } else {
                        empPersonalInfo.setIsAnyDisciplinaryProcedingStr("No");
                    }

                    model.addAttribute("employeePersonalObj", empPersonalInfo);

                }

                List<EmpContactDtl> empContactDtl1 = empContactRepository.findByEmpIdOrderByIdDesc(id);
                EmpContactDtl empContactDt = null;
                if (empContactDtl1 != null && empContactDtl1.size() > 0)
                    empContactDt = empContactDtl1.get(0);
                if (empContactDt != null) {
                    model.addAttribute("employeeContactObj", empContactDt);
                }

                List<EmpHealthDtl> empHealthDt1 = empHealthRepository.findByEmpIdOrderByIdDesc(id);
                EmpHealthDtl empHealthDtl = null;
                if (empHealthDt1 != null && empHealthDt1.size() > 0)
                    empHealthDtl = empHealthDt1.get(0);
                if (empHealthDtl != null) {
                    model.addAttribute("employeeHealthObj", empHealthDtl);

                    HrmsCode hrmsBloodGroup = hrmsCodeService.findByFieldNameAndCode("BLD_GRP",
                            empHealthDtl.getBloodGroup());
                    if (hrmsBloodGroup != null) {
                        empHealthDtl.setBloodGroupDesc(hrmsBloodGroup.getDescription());
                    }
                }

//				List<EmpFamilyDtl> empFamilyDtl = empFamilyRepository.findByEmpId(id);
//				model.addAttribute("empFamilyDtlObj", empFamilyDtl);

                List<EmpReportingOfficer> empOfficerList = empReportingOfficerRepository.findByEmpId(id);
                if (empOfficerList.size() > 0) {
                    model.addAttribute("employeeOfficer", empOfficerList.get((empOfficerList.size() - 1)));
                }

                List<Job> jobDetail1 = jobRepo.findByEmpIdOrderByIdDesc(id);
                Job jobDetail = null;
                if (jobDetail1 != null && jobDetail1.size() > 0)
                    jobDetail = jobDetail1.get(0);
                if (jobDetail != null) {
                    model.addAttribute("jobObj", jobDetail);
                    List<EmployeementCategory> empCatList = empCatService
                            .findByemployeeMentType(jobDetail.getEmployeementType());
                    model.addAttribute("empCatList", empCatList);
                    if ((jobDetail.getEmployeementType().getId() == 1 || jobDetail.getEmployeementType().getId() == 2)
                            && jobDetail.getEmployeementType().getId() != 4) {
                        model.addAttribute("isRetire", true);
                    } else if (jobDetail.getEmployeementType().getId() == 3
                            && jobDetail.getEmployeementType().getId() != 4) {
                        model.addAttribute("isContract", true);
                    } else if (jobDetail.getEmployeementType().getId() == 1
                            && jobDetail.getEmployeementType().getId() != 4) {
                        model.addAttribute("isConfirmation", true);
                    }

                } else {
                    model.addAttribute("jobObj", new Job());
                    model.addAttribute("isNull", true);
                }
            }

            auditTrailService.saveAuditTrailData("Employee", "AddPage", "Admin", NotificationModule.EMPLOYEE_INFO,
                    NotificationAction.ADD, "/addEmployee", userId);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in add employee " + e.getMessage());
        }
        return "hrms/HR/empmgmt/empinfo/viewEmployee";
    }

    @RequestMapping("/editEmployee/{id}")
    public String editEmployee(@PathVariable(value = "id") Long id, Model model, HttpSession session,
                               HttpServletRequest request) {
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");
            Long roleId = (Long) session.getAttribute("roleId");
            Optional<CompanyMaster> cm = null;
            cm = companyMasterRepository.findById(companyId);

            model.addAttribute("companySName", cm.get().getCompanyName());

            model.addAttribute("companyCode", cm.get().getCompanyCodePrefix());

            List<HrmsCode> addrTypeList = null;
            if (um == null || companyId == null || companyBranchId == null) {
                return "redirect:/signin";
            } else {

                if (um.getRoleMasterId().getIsAdmin()) {
                    model.addAttribute("isAdmin", true);
                } else {
                    model.addAttribute("isAdmin", false);
                }
                RoleMaster role =  roleMasterRepository.findByIdAndIsDelete(roleId, false);
                if (role.getRoleName().equals(CommonConstant.EMP_ROLE)) {
                    model.addAttribute("role", role.getRoleName());
                }

                model.addAttribute("companySName", cm.get().getCompanyName());

                model.addAttribute("companyCode", cm.get().getCompanyCodePrefix());
                // model.addAttribute("companySName", cm.get().getCompanyName());

                // emp info
                List<HrmsCode> salutationList = hrmsCodeService.findByFieldName("SALUTATION");
                model.addAttribute("salutationList", salutationList);

                List<HrmsCode> vehiLicenseList = hrmsCodeService.findByFieldName("VEHI_LIC_TYPE");
                model.addAttribute("vehiLicenseList", vehiLicenseList);

                List<HrmsCode> unitList = hrmsCodeService.findByFieldName("UNIT_TYPE");
                model.addAttribute("unitList", unitList);

                List<GroupMaster> groupMstList = groupMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("groupMstList", groupMstList);

                List<DepartmentMaster> deptMstList = departmentMasterRepository
                        .findAllByIsDeleteFalseAndStatusActiveAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                companyId, companyBranchId);
                model.addAttribute("deptMstList", deptMstList);

                List<DesignationMaster> designationMstList = designationMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("designationMstList", designationMstList);

                // personal info
                List<HrmsCode> genderList = hrmsCodeService.findByFieldName("GENDER");
                model.addAttribute("genderList", genderList);

                List<HrmsCode> maritalStatusList = hrmsCodeService.findByFieldName("MARITAL");
                model.addAttribute("maritalStatusList", maritalStatusList);

                List<HrmsCode> communityList = hrmsCodeService.findByFieldName("COMMUNITY");
                model.addAttribute("communityList", communityList);

                List<ReligionMaster> religionMstList = religionMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("religionMstList", religionMstList);

                List<CastMaster> castMstList = castMasterRepository
                        .findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(companyId,
                                companyBranchId);
                model.addAttribute("castMstList", castMstList);
//				List<CastMaster> castMstList = castMasterRepository
//						.findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(CommonConstant.ACTIVE,companyId,
//								companyBranchId);
//				model.addAttribute("castMstList", castMstList);

                List<CountryMaster> countryMstList = countryMasterRepository.findAllByIsDeleteFalseOrderByCountryName();
                model.addAttribute("countryMstList", countryMstList);

                List<BankMaster> bankMstList = bankMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("bankMstList", bankMstList);

                List<BankBranchMaster> bankBranchMstList = bankBranchMasterRepository
                        .findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(companyId,
                                companyBranchId);
                model.addAttribute("bankBranchMstList", bankBranchMstList);

//				List<HrmsCode> licenseTypeList = hrmsCodeService.findByFieldName("VEHI_LIC_TYPE");
//				model.addAttribute("licenseTypeList", licenseTypeList);

                List<VehicleTypeMaster> licenseTypeList = vehicleTypeMasterRepository
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("licenseTypeList", licenseTypeList);

                List<CountryMaster> citizenList = countryMasterRepository.findAllByIsDeleteFalseOrderByCitizenship();
                model.addAttribute("citizenList", citizenList);

                List<FamilyRelationMaster> familyRelationList = familyRelationMasterService
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByDisplayOrderAsc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("familyRelationList", familyRelationList);

                List<HrmsCode> bloodGroupList = hrmsCodeService.findByFieldName("BLD_GRP");
                model.addAttribute("bloodGroupList", bloodGroupList);

                List<HrmsCode> serviceList = hrmsCodeService.findByFieldName("SERVICE_TYPE");
                model.addAttribute("serviceList", serviceList);

                List<DisabilityTypeMaster> disabilityTypeList = disabilityTypeMasterService
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("disabilityTypeList", disabilityTypeList);

                List<StateMaster> stateMstList = stateMasterRepositry
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE);
                model.addAttribute("stateMstList", stateMstList);

                List<StateMaster> stateList = stateMasterRepositry.findByCountry(250L);
                model.addAttribute("stateList", stateList);

                // Attachments
                List<HrmsCode> documentList = hrmsCodeService.findByFieldName("ATTACHMENT_TYPE");
                model.addAttribute("documentList", documentList);

                model.addAttribute("attachmentsObj", new EmpAttachmentsDtl());

                // Education Qualification info
                List<EducationMaster> educationQualificationList = educationMasterService
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByDisplaySortOrderAsc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                model.addAttribute("educationQualificationList", educationQualificationList);

                List<HrmsCode> gradeList = hrmsCodeService.findByFieldName("GRADE");
                model.addAttribute("gradeHrmsList", gradeList);

                List<HrmsCode> modeOfStudyList = hrmsCodeService.findByFieldName("STUDY_MODE");
                model.addAttribute("modeOfStudyList", modeOfStudyList);

                model.addAttribute("statusList", statusMasterService.findAll());

                model.addAttribute("gradeList", gradeService
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId));
                model.addAttribute("paycommissionList", paycommissionService
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId));
                model.addAttribute("recruitmentTypeList", recruitmentTypeService.findAllByIsDeleteFalse());
                model.addAttribute("companyMasterList", compRepo.findAllByIsDeleteFalseOrderByCompanyName());
                List<EmployeementType> employeementTypeList = empTypeService.findALlByIsDeleteFalse();
                model.addAttribute("employeementTypeList", employeementTypeList);
                model.addAttribute("shiftPolicyMasterList", shiftPolicyMasterRepo
                        .findAllByCompanyIdAndCompanyBranchIdAndIsDeleteFalse(companyId, companyBranchId));

                List<HrmsCode> daTypes = hrmsCodeService.findByFieldName("DA_TYPE");
                model.addAttribute("daTypes", daTypes);

                EmpSalaryDtl empSalary = empSalaryDtlRepository.findByEmp(employeeRepository.getById(id));

                model.addAttribute("empSalary", empSalary);

                List<HrmsCode> monthList = hrmsCodeService.findByFieldName("MONTHS");
                model.addAttribute("monthList", monthList);

                if (empSalary != null) {
                    System.err.println(
                            "code --> " + empSalary.getPayStructCode() + " Rev No " + empSalary.getRevisonNo());
//					List<PayStructDetail> payStructDtlList = payStructureDetailsRepository
//							.findAllByPayStructCodeAndRevisonNoOrderByElemOrderAsc(empSalary.getPayStructCode(),
//									empSalary.getRevisonNo());
//					System.err.println("-----" + payStructDtlList.size());
//					model.addAttribute("elementSalaryList", payStructDtlList);

                    // List<PayStructTemplateDetail> list =
                    // payStructDetailRepository.findAllByPayStructureMasterisDelete(empSalary.getPayStructMaster().getId());
//					List<PayStructDetail> list = payStructureDetailsRepository
//							.findAllByPayStructCodeAndRevisonNoOrderByElemOrderAsc(empSalary.getPayStructCode(),
//									empSalary.getRevisonNo());

                    List<Object[]> list = payStructureDetailsRepository
                            .findAllByPayStructCodeAndRevisonNoAndCompanyIdAndCompanyBranchIdOrderByElemOrderAsc(
                                    empSalary.getPayStructCode(), empSalary.getRevisonNo(), companyId, companyBranchId);

                    List<EmpSalaryGridDto> finalDtoList = new ArrayList<>();

                    FormulaCalculationBaseEntity formula  = null;
                    for (Object[] obj : list) {
                        EmpSalaryGridDto dto = new EmpSalaryGridDto();
                        BigInteger elementid = (BigInteger) obj[0];
                        BigInteger roundId = (BigInteger) obj[1];
                        Double elementAmount = (double) obj[2];
                        BigInteger formulaid = (BigInteger) obj[3];
                        ElementOfPaySystemMasterEntity eopsme = elementOfPaySys.findById(elementid.longValue()).orElse(null);
                        if(formulaid != null) {
                            formula = formulaCreationRepository.findById(formulaid.longValue()).orElse(null);
                        }
                        RoundMaster round = roundMasterRepo.findById(roundId.longValue()).orElse(null);
                        dto.setNameOfElement(eopsme.getNameOfElement());
                        dto.setElementNature(eopsme.getPayElementNature());
                        dto.setElementType(eopsme.getElementType());
                        dto.setElementShortName(eopsme.getShortNameOfElement());
                        if (null != formula) {
                            dto.setFormulaName(formula.getNameOfFormula());
                        } else {
                            // dto.setFormulaName("-");
                            EmpApproachMst empApproachMst = empApproachMstRepository
                                    .findTopByEmployeeIdOrderByIdDesc(id, companyId, companyBranchId).orElse(null);
                            if (empApproachMst != null) {
                                EmpApproachDtl empApproachDtl = empApproachDtlRepository
                                        .findByEmpApproachMstIdAndElementId(empApproachMst.getId(),
                                                eopsme.getId());
                                if (empApproachDtl != null) {
                                    dto.setFormulaName(empApproachDtl.getFormula());
                                }
                            } else {
                                dto.setFormulaName("-");
                            }

                        }

                        dto.setAmount(elementAmount);

                        if (round != null) {
                            dto.setRoundType(round.getRoundType());
                        } else {
                            dto.setRoundType("No Rounding");
                        }

                        dto.setAddInGross(eopsme.isGross());

                        dto.setElementId(eopsme.getId());
                        if (formula != null) {
                            dto.setFormulaId(formula.getFormulaId());
                        }
                        if (round != null) {
                            dto.setRoundMasterId(round.getId());
                        }
                        finalDtoList.add(dto);
                    }







                    /* Real Code by whoever developer */
                    /* ( */
//							for (PayStructDetail payStruct : list) {
//								EmpSalaryGridDto dto = new EmpSalaryGridDto();
//								dto.setNameOfElement(payStruct.getPayElement().getNameOfElement());
//								dto.setElementNature(payStruct.getPayElement().getPayElementNature());
//								dto.setElementType(payStruct.getPayElement().getElementType());
//								dto.setElementShortName(payStruct.getPayElement().getShortNameOfElement());
//								if (null != payStruct.getFormula()) {
//									dto.setFormulaName(payStruct.getFormula().getNameOfFormula());
//								} else {
//									// dto.setFormulaName("-");
//									EmpApproachMst empApproachMst = empApproachMstRepository
//											.findTopByEmployeeIdOrderByIdDesc(id, companyId, companyBranchId).orElse(null);
//									if (empApproachMst != null) {
//										EmpApproachDtl empApproachDtl = empApproachDtlRepository
//												.findByEmpApproachMstIdAndElementId(empApproachMst.getId(),
//														payStruct.getPayElement().getId());
//										if (empApproachDtl != null) {
//											dto.setFormulaName(empApproachDtl.getFormula());
//										}
//									} else {
//										dto.setFormulaName("-");
//									}
//
//								}
//
//								dto.setAmount(payStruct.getPayElementAmount());
//
//								if (payStruct.getRoundMaster() != null) {
//									dto.setRoundType(payStruct.getRoundMaster().getRoundType());
//								} else {
//									dto.setRoundType("No Rounding");
//								}
//
//								dto.setAddInGross(payStruct.getPayElement().isGross());
//
//								dto.setElementId(payStruct.getPayElement().getId());
//								if (payStruct.getFormula() != null) {
//									dto.setFormulaId(payStruct.getFormula().getFormulaId());
//								}
//								if (payStruct.getRoundMaster() != null) {
//									dto.setRoundMasterId(payStruct.getRoundMaster().getId());
//								}
//								finalDtoList.add(dto);
//							}
                    /* ) */
                    System.out.println("Final Element List===>" + finalDtoList);
                    System.out.println("Final Element List length===>" + finalDtoList.size());
                    System.out.println("Element List length===>" + list.size());

                    // EmpApproachMst empApproachMst =
                    // empApproachMstRepository.findTopByEmployeeIdOrderByIdDesc(id).orElse(null);
//					if(empApproachMst != null) {
//						List<EmpApproachDtl> empApproachDtl2 = empApproachDtlRepository.findByEmpApproachMstId(empApproachMst.getId());
//						if(empApproachDtl2.size() > 0) {
//							for(EmpApproachDtl ead2 : empApproachDtl2) {
//								EmpSalaryGridDto dto = new EmpSalaryGridDto();
//								dto.setNameOfElement(ead2.getElement().getNameOfElement());
//								dto.setElementNature(ead2.getElement().getPayElementNature());
//								dto.setElementType(ead2.getElement().getElementType());
//								dto.setElementShortName(ead2.getElement().getShortNameOfElement());
//								if (null != ead2.getFormula()) {
//									dto.setFormulaName(ead2.getFormula());
//								} else {
//									dto.setFormulaName("-");
//								}
//
//								//dto.setAmount(ead2.);
//
//								if (ead2.getRoundMaster() != null) {
//									dto.setRoundType(ead2.getRoundMaster().getRoundType());
//								} else {
//									dto.setRoundType("No Rounding");
//								}
//
//								dto.setAddInGross(ead2.getElement().isGross());
//
//								dto.setElementId(ead2.getElement().getId());
//								dto.setFormulaId(null);
//								if (ead2.getRoundMaster() != null) {
//									dto.setRoundMasterId(ead2.getRoundMaster().getId());
//								}
//								finalDtoList.add(dto);
//							}
//						}
//					}
                    model.addAttribute("elementSalaryList", finalDtoList);
                }

                // address info
                addrTypeList = hrmsCodeService.findByFieldName("ADDRESS_TYPE");

            }

            Optional<Employee> em = employeeRepository.findById(id);
            if (em.isPresent()) {
                String postEmpCode = "";

                if (em.get().getEmpCode().contains("/")) {
                    String[] empCode = SilverUtil.splitStringFromChar(em.get().getEmpCode(), "/");

                    String prefix = em.get().getEmpCodePrefix();
                    if (StringUtil.isNotEmpty(prefix)) {
                        postEmpCode = empCode[1];
                    } else {
                        postEmpCode = empCode[0];
                    }
                } else {
                    postEmpCode = em.get().getEmpCode();
                }

                EmpPhotoDtl empPhoto = empPhotoDtlRepository.findByEmpId(id);

                if (empPhoto != null && empPhoto.getProfileImg() != null) {
                    File file = new File(environment.getProperty("file.repository.hrms.path") + companyId
                            + File.separator + companyBranchId + File.separator + CommonConstant.EMP_FILES
                            + File.separator + empPhoto.getProfileImg().getFileNameView());
                    if (file != null && file.exists()) {
                        byte[] byteArry = CommonUtility.toByteArray(file);
                        String userProfileImg = javax.xml.bind.DatatypeConverter.printBase64Binary(byteArry);
                        em.get().setEmpProfileImg(userProfileImg);
                    }
                }

                model.addAttribute("employeeObj", em.get());
                model.addAttribute("tempEmpCode", postEmpCode);
            }

            List<EmpPersonalInfo> empPersonalInfo1 = empPersonalInfoRepository.findByEmpIdOrderByIdDesc(id);
            EmpPersonalInfo empPersonalInfo = null;
            if (empPersonalInfo1 != null && empPersonalInfo1.size() > 0)
                empPersonalInfo = empPersonalInfo1.get(0);

            if (empPersonalInfo != null) {

                if (empPersonalInfo.getReligion() != null && empPersonalInfo.getReligion().getId() != null) {
                    List<CastMaster> castMstList = castMasterRepository
                            .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndReligionIdIdAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                    CommonConstant.ACTIVE, empPersonalInfo.getReligion().getId(), companyId,
                                    companyBranchId);
                    model.addAttribute("castMstList", castMstList);
                }

                model.addAttribute("employeePersonalObj", empPersonalInfo);

            } else {

                if (um.getRoleMasterId().getIsAdmin()) {
                    model.addAttribute("employeePersonalObj", new EmpPersonalInfo());
                } else {
                    EmpPersonalInfoTemp empPersonalInfoTemp = empPersonalInfoTempRepository.findByEmpIdAndIsApprovedFalse(id);

                    if (empPersonalInfoTemp != null) {
                        model.addAttribute("employeePersonalObj", empPersonalInfoTemp);
                    } else {
                        model.addAttribute("employeePersonalObj", new EmpPersonalInfo());
                    }
                }
            }

            List<EmpContactDtl> empContact1 = empContactRepository.findByEmpIdOrderByIdDesc(id);
            EmpContactDtl empContact = null;
            if (empContact1 != null && empContact1.size() > 0)
                empContact = empContact1.get(0);

            if (empContact != null) {
                model.addAttribute("employeeContactObj", empContact);
            }
            /*
             * else { model.addAttribute("employeeContactObj", new EmpContactDtl());
             * EmpContactDtlTemp empContactInfoTemp =
             * empContactDtlTempRepository.findByEmpIdAndIsApprovedFalse(id); if
             * (empContactInfoTemp != null) { model.addAttribute("employeeContactObj",
             * empContactInfoTemp); } else { model.addAttribute("employeeContactObj", new
             * EmpContactDtlTemp()); } }
             */

            else {
                if (um.getRoleMasterId().getIsAdmin()) {
                    model.addAttribute("employeeContactObj", new EmpContactDtl());
                } else {
                    EmpContactDtlTemp empContactInfoTemp = empContactDtlTempRepository.findByEmpIdAndIsApprovedFalse(id);

                    if (empContactInfoTemp != null) {
                        model.addAttribute("employeeContactObj", empContactInfoTemp);
                        model.addAttribute("isTempContactData", true);
                    } else {
                        model.addAttribute("employeeContactObj", new EmpContactDtlTemp());
                        model.addAttribute("isTempContactData", true);
                    }

                }
            }

            model.addAttribute("familyMasterObj", new EmpFamilyDtl());

            model.addAttribute("reportingOfficerObj", new EmpReportingOfficer());
            model.addAttribute("employeeEmergencyObj", new EmpEmergencyDtl());
            model.addAttribute("previousEmploymentObj", new EmpPreviousEmployment());

            Employee employee = employeeRepository.findById(id).get();

            DesignationMaster designationMaster = employee.getDesignation();

            if (designationMaster.getDesgIrfclLevels() != null) {
                Long IrfclId = designationMaster.getDesgIrfclLevels().getId();
                model.addAttribute("irfclLevelId", IrfclId);
            }

            EmpApproachMst empApproachMst = empApproachMstRepository
                    .findTopByEmployeeIdOrderByIdDesc(id, companyId, companyBranchId).orElse(null);
            List<EmpApproachDtl> empApproachDtls = null;

            if (empApproachMst != null) {
                empApproachDtls = empApproachDtlRepository.findByEmpApproachMstId(empApproachMst.getId());
                System.out.println("Approach Details : " + empApproachDtls);
                model.addAttribute("empApproachDtls", empApproachDtls);
                model.addAttribute("empApproachMaster", empApproachMst);
            } else {
                model.addAttribute("empApproachDtls", empApproachDtls);
                model.addAttribute("empApproachMaster", new EmpApproachMst());
            }

            model.addAttribute("employeeId", id);
            // model.addAttribute("empApproachMaster",
            // empApproachMstRepository.findByEmployeeId(id));

            model.addAttribute("irfclApproachList", irfclApproachService.findAll(companyId));
            model.addAttribute("fixedApproachList",
                    elementOfPaySystemMasterRepository.findAllByFixedElement(companyId));

            List<EmpHealthDtl> empHealth1 = empHealthRepository.findByEmpIdOrderByIdDesc(id);
            EmpHealthDtl empHealth = null;
            if (empHealth1 != null && empHealth1.size() > 0)
                empHealth = empHealth1.get(0);
            if (empHealth != null) {
                model.addAttribute("employeeHealthObj", empHealth);
            } else {
                if (um.getRoleMasterId().getIsAdmin()) {
                    model.addAttribute("employeeHealthObj", new EmpHealthDtl());
                } else {
                    EmpHealthDtlTemp empHealthTemp = empHealthTempRepository.findByEmpIdAndIsApprovedFalse(id);

                    if (empHealthTemp != null) {
                        model.addAttribute("employeeHealthObj", empHealthTemp);
                        model.addAttribute("isTempHealthData", true);
                    } else {
                        model.addAttribute("employeeHealthObj", new EmpHealthDtl());
                        model.addAttribute("isTempHealthData", true);
                    }
                }
            }

            model.addAttribute("educationQualificationObj", new EmpEducationQualificationDtl());

            model.addAttribute("employeeSalaryObj", new EmpSalaryDtl());

            List<Job> jobDetail1 = jobRepo.findByEmpIdOrderByIdDesc(id);
            Job jobDetail = null;
            if (jobDetail1 != null && jobDetail1.size() > 0)
                jobDetail = jobDetail1.get(0);
            if (jobDetail != null) {
                model.addAttribute("jobObj", jobDetail);
                List<EmployeementCategory> empCatList = empCatService
                        .findByemployeeMentType(jobDetail.getEmployeementType());
                model.addAttribute("empCatList", empCatList);
                if ((jobDetail.getEmployeementType().getId() == 1 || jobDetail.getEmployeementType().getId() == 2)
                        && jobDetail.getEmployeementType().getId() != 4) {
                    model.addAttribute("isRetire", true);
                } else if (jobDetail.getEmployeementType().getId() == 3
                        && jobDetail.getEmployeementType().getId() != 4) {
                    model.addAttribute("isContract", true);
                } else if (jobDetail.getEmployeementType().getId() == 1
                        && jobDetail.getEmployeementType().getId() != 4) {
                    model.addAttribute("isConfirmation", true);
                }

            } else {
                model.addAttribute("jobObj", new Job());
                model.addAttribute("isNull", true);
            }

            model.addAttribute("isEdit", true);

            List<CountryMaster> countryList = countryMasterRepository.findAllByIsDeleteFalseOrderByCountryName();
            model.addAttribute("countryList", countryList);

            model.addAttribute("employeeAddressObj", new EmpAddressDtl());

            List<EmpAddressDtl> empAppList = empAddressRepository.findByEmpId(id);

            for (EmpAddressDtl empAddressDtl : empAppList) {
                addrTypeList.removeIf(x -> x.getCode().equalsIgnoreCase(empAddressDtl.getAddType()));
            }

            model.addAttribute("addrTypeList", addrTypeList);
            if (empAppList.size() > 0) {

                for (EmpAddressDtl empAddressDtl : empAppList) {
                    HrmsCode hrmsCodeAddr = hrmsCodeService.findByFieldNameAndCode("ADDRESS_TYPE",
                            empAddressDtl.getAddType());
                    if (hrmsCodeAddr != null) {
                        empAddressDtl.setAddTypeStr(hrmsCodeAddr.getDescription());
                    }

                    List<StateMaster> stateList = stateMasterRepositry
                            .findByCountryIdAndStatusIdStatusNameIgnoreCaseAndIsDeleteFalseOrderByCreatedDateDesc(
                                    empAddressDtl.getCountry().getId(), CommonConstant.ACTIVE);
                    model.addAttribute("stateList", stateList);

//					List<DistrictMaster> districtList = districtMasterRepository
//							.findByStateIdAndIsDeleteFalseOrderByCreatedDateDesc(empAddressDtl.getState().getId());
                    List<DistrictMaster> districtList = districtMasterRepository
                            .findByStateIdIdAndStatusIdStatusNameIgnoreCaseAndIsDeleteFalseOrderByDistrictNameAsc(
                                    empAddressDtl.getState().getId(), CommonConstant.ACTIVE);
                    model.addAttribute("districtList", districtList);

//					List<CityMaster> cityList = cityMasterRepository
//							.findByDistrictIdAndIsDeleteFalseOrderByCreatedDateDesc(empAddressDtl.getDistrict().getId());
                    List<CityMaster> cityList = cityMasterRepository
                            .findByDistrictIdIdAndStatusIdStatusNameIgnoreCaseAndIsDeleteFalseOrderByCityNameAsc(
                                    empAddressDtl.getDistrict().getId(), CommonConstant.ACTIVE);
                    model.addAttribute("cityList", cityList);
                }
                model.addAttribute("empAppList", empAppList);
            }

            EmpPhotoDtl empPhotoDtl = empPhotoDtlRepository.findByEmpId(id);
            if (empPhotoDtl != null) {
                model.addAttribute("employeeProfileObj", empPhotoDtl);
            } else {
                if (um.getRoleMasterId().getIsAdmin()) {
                    model.addAttribute("employeeProfileObj", new EmpPhotoDtl());
                } else {
                    EmpPhotoDtlTemp empPhotoTemp = empPhotoDtlTempRepository.findByEmpIdAndIsApprovedFalse(id);

                    if (empPhotoTemp != null) {
                        model.addAttribute("employeeProfileObj", empPhotoTemp);
                    } else {
                        model.addAttribute("employeeProfileObj", new EmpPhotoDtl());
                    }
                }
            }

            model.addAttribute("nomineeObj", new Nominee());

            List<NomineeTypeMaster> nomineeTypeMasterList = nomineeTypeMasterRepo
                    .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                            CommonConstant.ACTIVE, companyId, companyBranchId);
            model.addAttribute("nomineeTypeMasterList", nomineeTypeMasterList);
            auditTrailService.saveAuditTrailData("Employee", "EditPage", "Admin", NotificationModule.EMPLOYEE_INFO,
                    NotificationAction.ADD, "/editEmployee", userId);

            List<Employee> employees = employeeRepository
                    .findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchId(companyId, companyBranchId);
            System.err.println(employees.size());
            model.addAttribute("employees", employees);

            List<RoundMaster> roundList = roundMasterRepo.findAll();
            model.addAttribute("roundList", roundList);

//			model.addAttribute("peElementTypeList", elementOfPaySys.findAllByActiveTrueOrderByIdDesc());

            List<ElementOfPaySystemMasterEntity> peElementTypeList = elementOfPaySys
                    .findAllIsDeleteFalseByCompanyAndCompanyBranchAndElementType(companyId, companyBranchId);
            model.addAttribute("peElementTypeList", peElementTypeList);

            model.addAttribute("payUtegisMaster",
                    payutegismasterRepo.findTop2ByCompanyAndCompanyBranchAndIsDeleteFalseOrderByEffectiveDateDesc(
                            companyMasterRepository.findById(companyId).orElse(null),
                            companyBranchMasterRepository.findById(companyBranchId).orElse(null)));

            model.addAttribute("codes", hrmsCodeService.findByFieldName("HRAAdmissibility"));

            model.addAttribute("das",
                    daConfigurationRepository.findAllByCompanyAndCompanyBranchAndIsDeleteFalse(
                            companyMasterRepository.findById(companyId).orElse(null),
                            companyBranchMasterRepository.findById(companyBranchId).orElse(null)));

            model.addAttribute("EEFcodes", hrmsCodeService.findByFieldName("EMPElgibleFor"));

            model.addAttribute("pf", providentFundRepository.findByLastRecord(companyId, companyBranchId));

            List<EmpReportingOfficer> empOfficerList = empReportingOfficerRepository.findByEmpId(id);
            model.addAttribute("empOfficerList", empOfficerList);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in edit employee " + e.getMessage());
        }
        return "hrms/HR/empmgmt/empinfo/addEmployee";
    }

    @Transactional
    @PostMapping(value = "/saveEmpInfo")
    public @ResponseBody JsonResponse saveEmpInfo(HttpServletRequest request, HttpSession session, Model model,
                                                  @ModelAttribute("employeeObj") Employee employeeObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            String doaStr = request.getParameter("dateOfAppointmentStr");
            if (StringUtil.isNotEmpty(doaStr)) {
                employeeObj.setDateOfAppointment(DateUtil.convertStringToDate(doaStr, DateUtil.IST_DATE_FORMATE));
            }

            EmployeeValidator validatior = new EmployeeValidator();
            validatior.validate(employeeObj, result);
            if (result.hasErrors()) {
                model.addAttribute("employeeObj", employeeObj);

                res.setStatus("FAIL");

                Map<String, String> errors = new HashMap<String, String>();
                errors = result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                res.setResult(errors);
                return res;
            }

            Optional<CompanyMaster> cm = null;
            if (companyId != null) {
                cm = companyMasterRepository.findById(companyId);
                if (cm.isPresent()) {
                    employeeObj.setCompany(cm.get());

                }
            }
            Optional<CompanyBranchMaster> cbm = null;
            if (companyBranchId != null) {
                cbm = companyBranchMasterRepository.findById(companyBranchId);
                if (cbm.isPresent()) {
                    employeeObj.setCompanyBranch(cbm.get());
                }
            }

            employeeObj.setAppId(1L);

            String flag = employeeObj.getFlagForApproachSelection();
            employeeObj.setFlagForApproachSelection(flag);
            String empCode = "";

            if (employeeObj.getId() == null) {

                StatusMaster status = statusMasterRepository.findByStatusName(CommonConstant.ACTIVE);
                RoleMaster roleDetails = roleMasterRepository
                        .findByRoleNameAndIsDeleteFalseAndStatusIdAndCompIdAndBranchId("emp", status.getId(), companyId,
                                companyBranchId);
                if (roleDetails == null) {
                    res.setStatus("RoleNotCreated");
                    return res;
                } else {

//				employeeObj.setEmpCodePrefix(employeeObj.getEmpCodePrefix());
//				employeeObj.setEmpCodePostfix(employeeObj.getEmpCodePostfix());
//				if (StringUtil.isNotEmpty(employeeObj.getEmpCodePrefix())) {
//					cm = companyMasterRepository.findById(companyId);
//					if(!cm.get().getCompanyShortName().toLowerCase().contains("irfcl")) {
//						employeeObj.setEmpCode(employeeObj.getEmpCodePrefix() + "/" + employeeObj.getEmpCodePostfix());
//					}
//					else {
//						employeeObj.setEmpCode(employeeObj.getEmpCodePostfix());
//					}
//				}
//				else {
//					employeeObj.setEmpCode(employeeObj.getEmpCodePostfix());
//				}
                    employeeObj.setEmpCodePrefix(employeeObj.getEmpCodePrefix());
                    employeeObj.setEmpCodePostfix(employeeObj.getEmpCodePostfix());

                    if (StringUtil.isNotEmpty(employeeObj.getEmpCodePrefix())) {
                        cm = companyMasterRepository.findById(companyId);
                        if (!cm.get().getCompanyShortName().toLowerCase().contains("irfcl")) {
                            // For companies other than "irfcl"
                            empCode = employeeObj.getEmpCodePrefix() + "/" + employeeObj.getEmpCodePostfix();
                            employeeObj
                                    .setEmpCode(employeeObj.getEmpCodePrefix() + "/" + employeeObj.getEmpCodePostfix());
                        } else {
                            empCode = "IR" + "" + employeeObj.getEmpCodePostfix();
                            // For "irfcl" company
//							employeeObj
//									.setEmpCode(employeeObj.getEmpCodePrefix() + "" + employeeObj.getEmpCodePostfix());
                            employeeObj
                                    .setEmpCode(employeeObj.getEmpCodePostfix());
                        }
                    } else {
                        // If empCodePrefix is empty, set empCodePostfix as empCode
                        employeeObj.setEmpCode(employeeObj.getEmpCodePostfix());
                    }

                    employeeObj.setCreatedBy(um.getId());
                    employeeObj.setFlagForApproachSelection("Y");
                    employeeObj.setIpAddress(ClientIpUtil.getClientIp(request));
                    Employee em = employeeRepository.save(employeeObj);

                    DesignationMaster dm = designationMasterRepository.findById(em.getDesignation().getId())
                            .orElse(null);

                    if (em != null) {

                        UserMaster userMaster = new UserMaster();
                        userMaster.setUserId(empCode);
                        userMaster.setPassword(passwordEncoder.encode("Admin@123"));
                        userMaster.setCompany(cm.get());
                        userMaster.setCompanyBranch(cbm.get());
                        userMaster.setEmpId(employeeObj);
                        Long comBranchId = cbm.get().getId();
                        userMaster.setComanyBranchId(comBranchId.toString());

                        Long comId = cm.get().getId();
                        userMaster.setComanyId(comId.toString());

                        String displayName = "";
                        displayName = em.getFirstName();
                        if (StringUtil.isNotEmpty(em.getLastName())) {
                            displayName = displayName + " " + em.getLastName();
                        }
                        userMaster.setUserName(displayName);
                        userMaster.setDisplayName(displayName);
                        userMaster.setSystemRoleType("Employee");
                        RoleMaster rm = roleMasterRepository
                                .findByRoleNameAndIsDeleteFalseAndCompanyIdAndCompanyBranchId(CommonConstant.EMP_ROLE,
                                        comId, comBranchId);
                        if (rm != null) {
                            userMaster.setRoleMasterId(rm);
                            userMaster.setRoleId(rm.getId().toString());
                        }
                        userMaster.setIpAddress(ClientIpUtil.getClientIp(request));
                        userMasterRepository.save(userMaster);

                        em.setDesignation(dm);

                        Employee empObj = (Employee) em;
                        res.setObj(empObj);
                        res.setStatus("SUCCESS");
                    }

                    // Code for Workflow State machine

                    //workflowStateMchine(companyId, companyBranchId, um, em);

                    auditTrailService.saveAuditTrailData("Employee", "Save", "Admin", NotificationModule.EMPLOYEE_INFO,
                            NotificationAction.ADD, "/save", userId);
                }
            } else {
                employeeObj.setUpdatedBy(um.getId());
//				employeeObj.setEmpCodePrefix(employeeObj.getEmpCodePrefix());
//				employeeObj.setEmpCodePostfix(employeeObj.getEmpCodePostfix());
//				// employeeObj.setEmpCode(employeeObj.getEmpCodePrefix() + "/"
//				// +employeeObj.getEmpCodePostfix());
//				if (StringUtil.isNotEmpty(employeeObj.getEmpCodePrefix())) {
//					employeeObj.setEmpCode(employeeObj.getEmpCodePrefix() + "/" + employeeObj.getEmpCodePostfix());
//				} else {
//					employeeObj.setEmpCode(employeeObj.getEmpCodePostfix());
//				}

                employeeObj.setEmpCodePrefix(employeeObj.getEmpCodePrefix());
                employeeObj.setEmpCodePostfix(employeeObj.getEmpCodePostfix());
//				if (StringUtil.isNotEmpty(employeeObj.getEmpCodePrefix())) {
//					cm = companyMasterRepository.findById(companyId);
//					if(!cm.get().getCompanyShortName().toLowerCase().contains("irfcl")) {
//						employeeObj.setEmpCode(employeeObj.getEmpCodePrefix() + "/" + employeeObj.getEmpCodePostfix());
//					}
//					else {
//						employeeObj.setEmpCode(employeeObj.getEmpCodePostfix());
//					}
//				}
//				else {
//					employeeObj.setEmpCode(employeeObj.getEmpCodePostfix());
//				}

                /* for NPS/GPS/Other */
                employeeObj.setEmployeeEligibleFor(employeeObj.getEmployeeEligibleFor());

                Employee emp = employeeRepository.findById(employeeObj.getId()).get();
                employeeObj.setEmpServiceStatus(emp.getEmpServiceStatus());
                if(emp.getEmployeeAppWorkflow() != null) {
                    employeeObj.setEmployeeAppWorkflow(emp.getEmployeeAppWorkflow());
                    employeeObj.setApproverStatus(emp.getApproverStatus());
                    employeeObj.setAprvId(emp.getAprvId());
                }
                employeeObj.setIpAddress(ClientIpUtil.getClientIp(request));
                Employee em = employeeRepository.save(employeeObj);

                if (em != null) {
                    res.setStatus("UPDATE");
                }
                auditTrailService.saveAuditTrailData("Employee", "Update", "Admin", NotificationModule.EMPLOYEE_INFO,
                        NotificationAction.UPDATE, "/save", userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update Employee information, " + e.getMessage());
        }
        return res;
    }

    @PostMapping(value = "/savePersonalInfo")
    public @ResponseBody JsonResponse savePersonalInfo(HttpServletRequest request, HttpSession session, Model model,
                                                       @ModelAttribute("employeePersonalObj") EmpPersonalInfo employeePersonalObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            // if user is admin then store in master table and if user is employee then
            // store in temp table
            if (um.getRoleMasterId().getIsAdmin()) {

                String dobStr = request.getParameter("dateOfBirthStr");
                if (StringUtil.isNotEmpty(dobStr)) {
                    employeePersonalObj.setDateOfBirth(DateUtil.convertStringToDate(dobStr, DateUtil.IST_DATE_FORMATE));
                }

                String domStr = request.getParameter("dateOfMarriageStr");
                if (StringUtil.isNotEmpty(domStr)) {
                    employeePersonalObj
                            .setDateOfMarriage(DateUtil.convertStringToDate(domStr, DateUtil.IST_DATE_FORMATE));
                }

                String doeStr = request.getParameter("dateOfExpiryStr");
                if (StringUtil.isNotEmpty(doeStr)) {
                    employeePersonalObj
                            .setDateOfExpiry(DateUtil.convertStringToDate(doeStr, DateUtil.IST_DATE_FORMATE));
                }

                String validUptoStr = request.getParameter("validUptoStr");
                if (StringUtil.isNotEmpty(validUptoStr)) {
                    employeePersonalObj
                            .setValidUpto(DateUtil.convertStringToDate(validUptoStr, DateUtil.IST_DATE_FORMATE));
                }

                EmpPersonalInfoValidator validatior = new EmpPersonalInfoValidator();
                validatior.validate(employeePersonalObj, result);
                if (result.hasErrors()) {
                    model.addAttribute("employeePersonalObj", employeePersonalObj);

                    res.setStatus("FAIL");

                    Map<String, String> errors = new HashMap<String, String>();
                    errors = result.getFieldErrors().stream()
                            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                    res.setResult(errors);
                    return res;
                }

                if (employeePersonalObj.getCountry() == null || employeePersonalObj.getCountry().getId() == null) {
                    employeePersonalObj.setCountry(null);
                }

                if (employeePersonalObj.getState() == null || employeePersonalObj.getState().getId() == null) {
                    employeePersonalObj.setState(null);
                }

                if (employeePersonalObj.getReligion() == null || employeePersonalObj.getReligion().getId() == null) {
                    employeePersonalObj.setReligion(null);
                }

                if (employeePersonalObj.getCast() == null || employeePersonalObj.getCast().getId() == null) {
                    employeePersonalObj.setCast(null);
                }

                if (request.getParameter("govtVehicle") != null) {
                    employeePersonalObj.setDtlGovtVehicle(employeePersonalObj.getDtlGovtVehicle());
                    employeePersonalObj.setIsgovtVehicleUseOD(true);
                } else {
                    employeePersonalObj.setIsgovtVehicleUseOD(false);
                    employeePersonalObj.setDtlGovtVehicle(null);
                }

                if (request.getParameter("isResidentOtherCountry") != null) {
                    if (employeePersonalObj.getOtherCountry() == null
                            || employeePersonalObj.getOtherCountry().getId() == null) {
                        employeePersonalObj.setOtherCountry(null);
                    }
                    employeePersonalObj.setOtherCountryAdd(employeePersonalObj.getOtherCountryAdd());
                    String dateOfMigrationStr = request.getParameter("dateOfMigrationStr");
                    if (StringUtil.isNotEmpty(dateOfMigrationStr)) {
                        employeePersonalObj.setDateOfMigration(
                                DateUtil.convertStringToDate(dateOfMigrationStr, DateUtil.IST_DATE_FORMATE));
                    }
                } else {
                    employeePersonalObj.setDateOfMigration(null);
                    employeePersonalObj.setOtherCountry(null);
                    employeePersonalObj.setOtherCountryAdd(null);
                }

                if (request.getParameter("isAnyDisciplinaryProceding") != null) {
                    employeePersonalObj.setDisciplinaryProcedingDtl(employeePersonalObj.getDisciplinaryProcedingDtl());
                } else {
                    employeePersonalObj.setDisciplinaryProcedingDtl(null);
                }

                if (employeePersonalObj.getId() == null) {

                    EmpPersonalInfo em = empPersonalInfoRepository.save(employeePersonalObj);
                    if (em != null) {
                        EmpPersonalInfo empObj = (EmpPersonalInfo) em;
                        res.setObj(empObj);
                        res.setStatus("SUCCESS");
                    }
                    auditTrailService.saveAuditTrailData("Employee personal info", "Save", "Admin",
                            NotificationModule.EMPLOYEE_PERSONAL_INFO, NotificationAction.ADD, "/save", userId);
                } else {

                    EmpPersonalInfo em = empPersonalInfoRepository.save(employeePersonalObj);
                    if (em != null) {
                        res.setStatus("UPDATE");
                    }
                    auditTrailService.saveAuditTrailData("Employee personal info", "Update", "Admin",
                            NotificationModule.EMPLOYEE_PERSONAL_INFO, NotificationAction.UPDATE, "/save", userId);
                }
            } else {

                EmpPersonalInfo empPersonalInfo = empPersonalInfoRepository.findByEmpId(um.getEmpId().getId());
                JsonResponse res1;
                if (empPersonalInfo == null) {
                    res1 = saveEmpPersonalInfoTemp(employeePersonalObj, request, result, model, userId);
                } else {
                    res1 = compareFieldsEmpPersonalInfoTemp(employeePersonalObj, request, result, model, userId);
                }
                return res1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update employee personal information, " + e.getMessage());
        }
        return res;
    }

//	@PostMapping(value = "/savePersonalInfo")
//	public @ResponseBody JsonResponse savePersonalInfo(HttpServletRequest request, HttpSession session, Model model,
//			@ModelAttribute("employeePersonalObj") EmpPersonalInfo employeePersonalObj, BindingResult result) {
//		JsonResponse res = new JsonResponse();
//		try {
//			Long userId = (long) session.getAttribute("userId");
//			UserMaster um = (UserMaster) session.getAttribute("usermaster");
//			Long companyId = (Long) session.getAttribute("companyId");
//			Long companyBranchId = (Long) session.getAttribute("companyBranchId");
//
//			if (um == null || companyId == null || companyBranchId == null) {
//				res.setStatus("FAIL");
//				return res;
//			}
//
//
//				String dobStr = request.getParameter("dateOfBirthStr");
//				if (StringUtil.isNotEmpty(dobStr)) {
//					employeePersonalObj.setDateOfBirth(DateUtil.convertStringToDate(dobStr, DateUtil.IST_DATE_FORMATE));
//				}
//
//				String domStr = request.getParameter("dateOfMarriageStr");
//				if (StringUtil.isNotEmpty(domStr)) {
//					employeePersonalObj.setDateOfMarriage(DateUtil.convertStringToDate(domStr, DateUtil.IST_DATE_FORMATE));
//				}
//
//				String doeStr = request.getParameter("dateOfExpiryStr");
//				if (StringUtil.isNotEmpty(doeStr)) {
//					employeePersonalObj.setDateOfExpiry(DateUtil.convertStringToDate(doeStr, DateUtil.IST_DATE_FORMATE));
//				}
//
//				String validUptoStr = request.getParameter("validUptoStr");
//				if (StringUtil.isNotEmpty(validUptoStr)) {
//					employeePersonalObj.setValidUpto(DateUtil.convertStringToDate(validUptoStr, DateUtil.IST_DATE_FORMATE));
//				}
//
//				EmpPersonalInfoValidator validatior = new EmpPersonalInfoValidator();
//				validatior.validate(employeePersonalObj, result);
//				if (result.hasErrors()) {
//					model.addAttribute("employeePersonalObj", employeePersonalObj);
//
//					res.setStatus("FAIL");
//
//					Map<String, String> errors = new HashMap<String, String>();
//					errors = result.getFieldErrors().stream()
//							.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
//					res.setResult(errors);
//					return res;
//				}
//
//				if (employeePersonalObj.getCountry() == null || employeePersonalObj.getCountry().getId() == null) {
//					employeePersonalObj.setCountry(null);
//				}
//
//				if (employeePersonalObj.getState() == null || employeePersonalObj.getState().getId() == null) {
//					employeePersonalObj.setState(null);
//				}
//
//				if (employeePersonalObj.getReligion() == null || employeePersonalObj.getReligion().getId() == null) {
//					employeePersonalObj.setReligion(null);
//				}
//
//				if (employeePersonalObj.getCast() == null || employeePersonalObj.getCast().getId() == null) {
//					employeePersonalObj.setCast(null);
//				}
//
//				if (request.getParameter("govtVehicle") != null) {
//					employeePersonalObj.setDtlGovtVehicle(employeePersonalObj.getDtlGovtVehicle());
//				} else {
//					employeePersonalObj.setIsgovtVehicleUseOD(false);
//					employeePersonalObj.setDtlGovtVehicle(null);
//				}
//
//				if (request.getParameter("isResidentOtherCountry") != null) {
//					if (employeePersonalObj.getOtherCountry() == null
//							|| employeePersonalObj.getOtherCountry().getId() == null) {
//						employeePersonalObj.setOtherCountry(null);
//					}
//					employeePersonalObj.setOtherCountryAdd(employeePersonalObj.getOtherCountryAdd());
//					String dateOfMigrationStr = request.getParameter("dateOfMigrationStr");
//					if (StringUtil.isNotEmpty(dateOfMigrationStr)) {
//						employeePersonalObj.setDateOfMigration(
//								DateUtil.convertStringToDate(dateOfMigrationStr, DateUtil.IST_DATE_FORMATE));
//					}
//				} else {
//					employeePersonalObj.setDateOfMigration(null);
//					employeePersonalObj.setOtherCountry(null);
//					employeePersonalObj.setOtherCountryAdd(null);
//				}
//
//				if (request.getParameter("isAnyDisciplinaryProceding") != null) {
//					employeePersonalObj.setDisciplinaryProcedingDtl(employeePersonalObj.getDisciplinaryProcedingDtl());
//				} else {
//					employeePersonalObj.setDisciplinaryProcedingDtl(null);
//				}
//
//				if (employeePersonalObj.getId() == null) {
//
//					EmpPersonalInfo em = empPersonalInfoRepository.save(employeePersonalObj);
//					if (em != null) {
//						EmpPersonalInfo empObj = (EmpPersonalInfo) em;
//						res.setObj(empObj);
//						res.setStatus("SUCCESS");
//
//						if (!um.getRoleMasterId().getIsAdmin()) {
//
//							Employee emp = employeeRepository.findById(em.getEmp().getId()).get();
//
//							PassEventDto eventDto = new PassEventDto();
//					    	eventDto.setWorkflowType(WorkflowType.EMPLOYEE_APPLICATION);
//					    	eventDto.setWorkflowInstanceId(emp.getEmployeeAppWorkflow());
//					    	eventDto.setEvent("E_RESET");
//					    	eventDto.setActionBy(um.getId());
//					    	List<EventResultDto> employee = employeeAppService.resetStateMachine(eventDto);
//
//					    	callEvent(emp.getEmployeeAppWorkflow(), "E_CREATE", "Changes Done By User", request, session);
//
//						}
//					}
//					auditTrailService.saveAuditTrailData("Employee personal info", "Save", "Admin",
//							NotificationModule.EMPLOYEE_PERSONAL_INFO, NotificationAction.ADD, "/save", userId);
//				} else {
//
//					EmpPersonalInfo em = empPersonalInfoRepository.save(employeePersonalObj);
//
//
//					if (!um.getRoleMasterId().getIsAdmin()) {
//
//						Employee emp = employeeRepository.findById(em.getEmp().getId()).get();
//
//						PassEventDto eventDto = new PassEventDto();
//				    	eventDto.setWorkflowType(WorkflowType.EMPLOYEE_APPLICATION);
//				    	eventDto.setWorkflowInstanceId(emp.getEmployeeAppWorkflow());
//				    	eventDto.setEvent("E_RESET");
//				    	eventDto.setActionBy(um.getId());
//				    	List<EventResultDto> employee = employeeAppService.resetStateMachine(eventDto);
//
//				    	callEvent(emp.getEmployeeAppWorkflow(), "E_CREATE", "Changes Done By User", request, session);
//
//					}
//					if (em != null) {
//						res.setStatus("UPDATE");
//					}
//					auditTrailService.saveAuditTrailData("Employee personal info", "Update", "Admin",
//							NotificationModule.EMPLOYEE_PERSONAL_INFO, NotificationAction.UPDATE, "/save", userId);
//				}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("Error in save or update employee personal information, " + e.getMessage());
//		}
//		return res;
//	}

    @RequestMapping(value = "/getEmpCat")
    public @ResponseBody JsonResponse getEmpCat(@RequestParam("id") Long employementType, HttpSession session) {
        logger.info("employementType id " + employementType);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }
        EmployeementType empType = empTypeService.findById(employementType);
        List<EmployeementCategory> empCatList = empCatService.findByemployeeMentType(empType);

        List<Object> empCatMstList = new ArrayList<Object>();
        empCatMstList.addAll(empCatList);
        if (empCatList.size() > 0) {
            res.setStatus("success");
            res.setObjList(empCatMstList);
        }

        return res;
    }

    // save Emergency

    @PostMapping(value = "/saveEmpEmergencyDtl")
    public @ResponseBody JsonResponse saveEmpEmergencyDtl(HttpServletRequest request, HttpSession session, Model model,
                                                          @ModelAttribute("employeeEmergencyObj") EmpEmergencyDtl employeeEmergencyObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            EmpEmergencyValidator validatior = new EmpEmergencyValidator();
            validatior.validate(employeeEmergencyObj, result);
            if (result.hasErrors()) {
                model.addAttribute("employeeEmergencyObj", employeeEmergencyObj);

                res.setStatus("FAIL");

                Map<String, String> errors = new HashMap<String, String>();
                errors = result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                res.setResult(errors);
                return res;
            }

            if (employeeEmergencyObj.getId() == null) {

                String priority = request.getParameter("priority");

                EmpEmergencyDtl empEmergencyDtl = empEmergencyRepository.findAllByPriority(priority,
                        employeeEmergencyObj.getEmp().getId());
                if (empEmergencyDtl != null) {
                    res.setStatus("PRIORITY");
                } else {

                    if (um.getRoleMasterId().getIsAdmin()) {
                        EmpEmergencyDtl em = empEmergencyRepository.save(employeeEmergencyObj);

                        if (em != null) {
                            EmpEmergencyDtl empObj = (EmpEmergencyDtl) em;
                            res.setObj(empObj);
                            res.setStatus("SUCCESS");
                        }
                    } else {
                        EmpEmergencyDtlTemp emergencyDtlTemp = new EmpEmergencyDtlTemp();

                        emergencyDtlTemp.setEmp(employeeEmergencyObj.getEmp());
                        emergencyDtlTemp.setFirstNameEmg(employeeEmergencyObj.getFirstNameEmg());
                        emergencyDtlTemp.setMiddleNameEmg(employeeEmergencyObj.getMiddleNameEmg());
                        emergencyDtlTemp.setLastNameEmg(employeeEmergencyObj.getLastNameEmg());
                        emergencyDtlTemp.setMobileNoEmg(employeeEmergencyObj.getMobileNoEmg());
                        emergencyDtlTemp.setFamilyRelationEmg(employeeEmergencyObj.getFamilyRelationEmg());
                        emergencyDtlTemp.setAddressEmg(employeeEmergencyObj.getAddressEmg());
                        emergencyDtlTemp.setPriority(priority);
                        emergencyDtlTemp.setPhoneNoEmg(employeeEmergencyObj.getPhoneNoEmg());
                        emergencyDtlTemp.setEmailEmg(employeeEmergencyObj.getEmailEmg());

                        EmpEmergencyDtlTemp em = empEmergencyDtlTempRepository.save(emergencyDtlTemp);

                        if (em != null) {
                            res.setObj(em);
                            res.setStatus("SUCCESS");
                        }
                    }

                    auditTrailService.saveAuditTrailData("Employee Emergency Information", "Save", "Admin",
                            NotificationModule.EMPLOYEE_Emergency_INFO, NotificationAction.ADD, "/save", userId);
                }
            } else {
                if (um.getRoleMasterId().getIsAdmin()) {
                    String priority = request.getParameter("priority");
                    EmpEmergencyDtl empEmergencyDtl = empEmergencyRepository.findAllByPriorityEdittime(priority,
                            employeeEmergencyObj.getEmp().getId(), Long.parseLong(request.getParameter("id")));

                    if (empEmergencyDtl != null) {
                        res.setStatus("PRIORITY");
                    } else {
                        EmpEmergencyDtl em = empEmergencyRepository.save(employeeEmergencyObj);
                        if (em != null) {
                            EmpEmergencyDtl empObj = (EmpEmergencyDtl) em;
                            res.setObj(empObj);
                            res.setStatus("UPDATE");
                        }
                    }
                } else {
                    if (request.getParameter("isTempEmergencyDetails").trim().isEmpty()) {
                        String priority = request.getParameter("priority");
                        EmpEmergencyDtl empEmergencyDtl = empEmergencyRepository.findById(employeeEmergencyObj.getId())
                                .get();

                        EmpEmergencyDtlTemp emergencyDtlTemp = new EmpEmergencyDtlTemp();

                        if (empEmergencyDtlTempRepository
                                .findByEmpEmergencyDtlMstIdAndIsApprovedFalse(employeeEmergencyObj.getId())
                                .isPresent()) {
                            emergencyDtlTemp = empEmergencyDtlTempRepository
                                    .findByEmpEmergencyDtlMstIdAndIsApprovedFalse(employeeEmergencyObj.getId()).get();
                        }

                        emergencyDtlTemp.setEmpEmergencyDtlMstId(employeeEmergencyObj.getId());
                        emergencyDtlTemp.setIsEditedRecord(true);
                        emergencyDtlTemp.setIsDeletedRecord(false);
                        emergencyDtlTemp.setEmp(employeeEmergencyObj.getEmp());

                        if (empEmergencyDtl.getEmpFamily() != null) {
                            emergencyDtlTemp.setEmpFamilyId(empEmergencyDtl.getEmpFamily().getId());
                        }
                        if (!Objects.equal(empEmergencyDtl.getFirstNameEmg(), employeeEmergencyObj.getFirstNameEmg()))
                            emergencyDtlTemp.setFirstNameEmg(employeeEmergencyObj.getFirstNameEmg());
                        else
                            emergencyDtlTemp.setFirstNameEmg(null);

                        if (!Objects.equal(
                                empEmergencyDtl.getMiddleNameEmg() == null ? "" : empEmergencyDtl.getMiddleNameEmg(),
                                employeeEmergencyObj.getMiddleNameEmg()))
                            emergencyDtlTemp.setMiddleNameEmg(employeeEmergencyObj.getMiddleNameEmg());
                        else
                            emergencyDtlTemp.setMiddleNameEmg(null);

                        if (!Objects.equal(
                                empEmergencyDtl.getLastNameEmg() == null ? "" : empEmergencyDtl.getLastNameEmg(),
                                employeeEmergencyObj.getLastNameEmg()))
                            emergencyDtlTemp.setLastNameEmg(employeeEmergencyObj.getLastNameEmg());
                        else
                            emergencyDtlTemp.setLastNameEmg(null);

                        if (!Objects.equal(
                                empEmergencyDtl.getMobileNoEmg() == null ? "" : empEmergencyDtl.getMobileNoEmg(),
                                employeeEmergencyObj.getMobileNoEmg()))
                            emergencyDtlTemp.setMobileNoEmg(employeeEmergencyObj.getMobileNoEmg());
                        else
                            emergencyDtlTemp.setMobileNoEmg(null);

                        if (!Objects.equal(
                                empEmergencyDtl.getFamilyRelationEmg() == null ? ""
                                        : empEmergencyDtl.getFamilyRelationEmg(),
                                employeeEmergencyObj.getFamilyRelationEmg()))
                            emergencyDtlTemp.setFamilyRelationEmg(employeeEmergencyObj.getFamilyRelationEmg());
                        else
                            emergencyDtlTemp.setFamilyRelationEmg(null);

                        if (!Objects.equal(
                                empEmergencyDtl.getAddressEmg() == null ? "" : empEmergencyDtl.getAddressEmg(),
                                employeeEmergencyObj.getAddressEmg()))
                            emergencyDtlTemp.setAddressEmg(employeeEmergencyObj.getAddressEmg());
                        else
                            emergencyDtlTemp.setAddressEmg(null);

                        if (!Objects.equal(empEmergencyDtl.getPriority() == null ? "" : empEmergencyDtl.getPriority(),
                                employeeEmergencyObj.getPriority()))
                            emergencyDtlTemp.setPriority(priority);
                        else
                            emergencyDtlTemp.setPriority(null);

                        if (!Objects.equal(
                                empEmergencyDtl.getPhoneNoEmg() == null ? "" : empEmergencyDtl.getPhoneNoEmg(),
                                employeeEmergencyObj.getPhoneNoEmg()))
                            emergencyDtlTemp.setPhoneNoEmg(employeeEmergencyObj.getPhoneNoEmg());
                        else
                            emergencyDtlTemp.setPhoneNoEmg(null);

                        if (!Objects.equal(empEmergencyDtl.getEmailEmg() == null ? "" : empEmergencyDtl.getEmailEmg(),
                                employeeEmergencyObj.getEmailEmg()))
                            emergencyDtlTemp.setEmailEmg(employeeEmergencyObj.getEmailEmg());
                        else
                            emergencyDtlTemp.setEmailEmg(null);

                        EmpEmergencyDtlTemp em = empEmergencyDtlTempRepository.save(emergencyDtlTemp);

                        if (em != null) {
                            res.setObj(em);
                            res.setStatus("UPDATE");
                        }
                    } else {
                        String priority = request.getParameter("priority");
                        EmpEmergencyDtlTemp emergencyDtlTemp = new EmpEmergencyDtlTemp();

                        if (empEmergencyDtlTempRepository.findById(employeeEmergencyObj.getId()).isPresent()) {
                            emergencyDtlTemp = empEmergencyDtlTempRepository.findById(employeeEmergencyObj.getId())
                                    .get();
                        }

                        emergencyDtlTemp.setEmpEmergencyDtlMstId(employeeEmergencyObj.getId());
                        emergencyDtlTemp.setEmp(employeeEmergencyObj.getEmp());
                        emergencyDtlTemp.setFirstNameEmg(employeeEmergencyObj.getFirstNameEmg());
                        emergencyDtlTemp.setMiddleNameEmg(employeeEmergencyObj.getMiddleNameEmg());
                        emergencyDtlTemp.setLastNameEmg(employeeEmergencyObj.getLastNameEmg());
                        emergencyDtlTemp.setMobileNoEmg(employeeEmergencyObj.getMobileNoEmg());
                        emergencyDtlTemp.setFamilyRelationEmg(employeeEmergencyObj.getFamilyRelationEmg());
                        emergencyDtlTemp.setAddressEmg(employeeEmergencyObj.getAddressEmg());
                        emergencyDtlTemp.setPriority(priority);
                        emergencyDtlTemp.setPhoneNoEmg(employeeEmergencyObj.getPhoneNoEmg());
                        emergencyDtlTemp.setEmailEmg(employeeEmergencyObj.getEmailEmg());

                        EmpEmergencyDtlTemp em = empEmergencyDtlTempRepository.save(emergencyDtlTemp);

                        if (em != null) {
                            res.setObj(em);
                            res.setStatus("UPDATE");
                        }
                    }

                }
                auditTrailService.saveAuditTrailData("Employee Emergency Information", "Update", "Admin",
                        NotificationModule.EMPLOYEE_Emergency_INFO, NotificationAction.UPDATE, "/save", userId);

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update employee emergency information, " + e.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "/editEmpEmergency")
    public @ResponseBody JsonResponse editEmpEmergency(@RequestParam("id") Long id, HttpSession session,
                                                       @RequestParam("isTempData") Boolean isTempData) {
        logger.info("employee emergency id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }
        if (um.getRoleMasterId().getIsAdmin()) {
            Optional<EmpEmergencyDtl> empEmergency = empEmergencyRepository.findById(id);
            if (empEmergency.isPresent()) {

                EmpEmergencyDtl employeeEmergencyObj = (EmpEmergencyDtl) empEmergency.get();
                res.setObj(employeeEmergencyObj);

                res.setStatus("SUCCESS");
            }
        } else {
            if (isTempData) {
                EmpEmergencyDtlTemp empEmergencyDtlTemp = empEmergencyDtlTempRepository.findById(id).orElse(null);

                if (empEmergencyDtlTemp != null) {
                    res.setTempTableData(true);

                    res.setObj(empEmergencyDtlTemp);
                    res.setStatus("SUCCESS");
                } else {
                    res.setStatus("FAIL");
                    return res;
                }

            } else {
                Optional<EmpEmergencyDtl> empEmergency = empEmergencyRepository.findById(id);
                if (empEmergency.isPresent()) {

                    EmpEmergencyDtl employeeEmergencyObj = (EmpEmergencyDtl) empEmergency.get();
                    res.setObj(employeeEmergencyObj);

                    res.setStatus("SUCCESS");
                }
            }
        }

        return res;
    }

    @RequestMapping(value = "/getEmergencyList/{id}")
    public @ResponseBody JsonResponse getEmergencyList(@PathVariable("id") Long id, Model model,
                                                       HttpServletRequest request, HttpSession session) {

        JsonResponse res = new JsonResponse();
        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        List<EmpEmergencyDtl> getEmergencyList = empEmergencyRepository.findByEmpIdOrderByPriorityAsc(id);

        List<Object> empEmgList = new ArrayList<Object>();
        empEmgList.addAll(getEmergencyList);
        if (empEmgList.size() > 0) {
            res.setStatus("success");
            res.setObjList(empEmgList);
        } else {
            if (!um.getRoleMasterId().getIsAdmin()) {

                List<EmpEmergencyDtlTemp> emergencyDtlTemps = empEmergencyDtlTempRepository
                        .findAllByEmpIdAndIsApprovedFalseAndEmpEmergencyDtlMstIdIsNull(id);
                if (emergencyDtlTemps.size() > 0) {
                    empEmgList.addAll(emergencyDtlTemps);
                    res.setStatus("success");
                    res.setObjList(empEmgList);
                    res.setTempTableData(true);
                }
            }
        }
        return res;
    }

    @GetMapping(value = "/deleteEmpEmergency/{id}")
    public @ResponseBody JsonResponse deleteEmpEmergency(@PathVariable("id") Long id, Model model,
                                                         HttpServletRequest request, HttpSession session, @RequestParam("isTempData") Boolean isTempData) {
        logger.info("EmployeeController.deleteEmpEmergency");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            if (um != null) {
                Optional<EmpEmergencyDtl> empEmergency = empEmergencyRepository.findById(id);

                if (um.getRoleMasterId().getIsAdmin()) {
                    if (empEmergency.isPresent()) {
                        List<EmpEmergencyDtl> empEmergencyList = empEmergencyRepository
                                .findByEmpIdOrderByPriorityAsc(empEmergency.get().getEmp().getId());
                        List<Object> empEmgList = new ArrayList<Object>();
                        empEmgList.addAll(empEmergencyList);
                        if (empEmgList.size() > 0) {
                            res.setObjList(empEmgList);
                        }

                        empEmergencyRepository.deleteById(id);
                        if (empEmergencyDtlTempRepository.findByEmpEmergencyDtlMstIdAndIsApprovedFalse(id)
                                .isPresent()) {
                            EmpEmergencyDtlTemp emergencyDtlTemp = empEmergencyDtlTempRepository
                                    .findByEmpEmergencyDtlMstIdAndIsApprovedFalse(id).get();
                            empEmergencyDtlTempRepository.deleteById(emergencyDtlTemp.getId());
                            res.setStatus("SUCCESS");
                        }

                        res.setStatus("SUCCESS");
                    } else {
                        res.setStatus("FAIL");
                    }
                } else {
                    if (isTempData) {

                        empEmergencyDtlTempRepository.deleteById(id);
                        res.setStatus("SUCCESS");
                    } else {
                        if (empEmergency.isPresent()) {
                            EmpEmergencyDtlTemp emergencyDtlTemp = new EmpEmergencyDtlTemp();

                            if (empEmergencyDtlTempRepository
                                    .findByEmpEmergencyDtlMstIdAndIsApprovedFalse(empEmergency.get().getId())
                                    .isPresent()) {
                                emergencyDtlTemp = empEmergencyDtlTempRepository
                                        .findByEmpEmergencyDtlMstIdAndIsApprovedFalse(empEmergency.get().getId()).get();
                            }
                            emergencyDtlTemp.setIsDeletedRecord(true);
                            emergencyDtlTemp.setEmpEmergencyDtlMstId(id);
                            emergencyDtlTemp.setIsEditedRecord(false);

                            emergencyDtlTemp.setFirstNameEmg(null);
                            emergencyDtlTemp.setMiddleNameEmg(null);
                            emergencyDtlTemp.setLastNameEmg(null);
                            emergencyDtlTemp.setMobileNoEmg(null);
                            emergencyDtlTemp.setFamilyRelationEmg(null);
                            emergencyDtlTemp.setAddressEmg(null);
                            emergencyDtlTemp.setPriority(null);
                            emergencyDtlTemp.setPhoneNoEmg(null);

                            EmpEmergencyDtlTemp em = empEmergencyDtlTempRepository.save(emergencyDtlTemp);

//							For save deleted record in History Table

                            EmpEmergencyDtlHistory empEmergencyDtlHistory = new EmpEmergencyDtlHistory();

                            empEmergencyDtlHistory.setEmp(empEmergency.get().getEmp());
                            empEmergencyDtlHistory.setIsDeletedRecord(true);
                            empEmergencyDtlHistory.setEmpEmergencyDtlMstId(em.getId());
                            empEmergencyDtlHistory.setIsEditedRecord(false);
                            empEmergencyDtlHistory.setFirstNameEmg(empEmergency.get().getFirstNameEmg());
                            empEmergencyDtlHistory.setMiddleNameEmg(empEmergency.get().getMiddleNameEmg());
                            empEmergencyDtlHistory.setLastNameEmg(empEmergency.get().getLastNameEmg());
                            empEmergencyDtlHistory.setMobileNoEmg(empEmergency.get().getPhoneNoEmg());
                            empEmergencyDtlHistory.setFamilyRelationEmg(empEmergency.get().getFamilyRelationEmg());
                            empEmergencyDtlHistory.setAddressEmg(empEmergency.get().getAddressEmg());
                            empEmergencyDtlHistory.setPriority(empEmergency.get().getPriority());
                            empEmergencyDtlHistory.setPhoneNoEmg(empEmergency.get().getPhoneNoEmg());

                            empEmergencyDtlHistoryRepository.save(empEmergencyDtlHistory);

                            if (em != null) {
                                res.setStatus("SUCCESS");
                            }
                        } else {
                            res.setStatus("FAIL");
                        }
                    }
                }
                auditTrailService.saveAuditTrailData("Employee Emergency Information", "Delete", "Admin",
                        NotificationModule.EMPLOYEE_Emergency_INFO, NotificationAction.DELETE, "/deleteEmpEmergency",
                        userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus("FAIL");
        }

        return res;
    }

    // previous employment

    @PostMapping(value = "/saveEmpPreviousEmployment")
    public @ResponseBody JsonResponse saveEmpPreviousEmployment(HttpServletRequest request, HttpSession session,
                                                                Model model, @ModelAttribute("previousEmploymentObj") EmpPreviousEmployment previousEmploymentObj,
                                                                BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            EmpPreviousEmploymentValidator validatior = new EmpPreviousEmploymentValidator();
            validatior.validate(previousEmploymentObj, result);
            if (result.hasErrors()) {
                model.addAttribute("previousEmploymentObj", previousEmploymentObj);

                res.setStatus("FAIL");

                Map<String, String> errors = new HashMap<String, String>();
                errors = result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                res.setResult(errors);
                return res;
            }

            if (previousEmploymentObj.getId() == null) {

                if (um.getRoleMasterId().getIsAdmin()) {
                    EmpPreviousEmployment em = empPreviousEmploymentRepository.save(previousEmploymentObj);
                    if (em != null) {
                        EmpPreviousEmployment empObj = (EmpPreviousEmployment) em;
                        res.setObj(empObj);
                        res.setStatus("SUCCESS");
                    }
                } else {
                    EmpPreviousEmploymentTemp employmentTemp = new EmpPreviousEmploymentTemp();

                    employmentTemp.setAddress(previousEmploymentObj.getAddress());
                    employmentTemp.setCompanyName(previousEmploymentObj.getCompanyName());
                    employmentTemp.setEmail(previousEmploymentObj.getEmail());
                    employmentTemp.setEmp(previousEmploymentObj.getEmp());
                    employmentTemp.setFromDate(previousEmploymentObj.getFromDate());
                    employmentTemp.setHrContactPerson(previousEmploymentObj.getHrContactPerson());
                    employmentTemp.setLastCTC(previousEmploymentObj.getLastCTC());
                    employmentTemp.setMobileNo(previousEmploymentObj.getMobileNo());
                    employmentTemp.setPhoneNo(previousEmploymentObj.getPhoneNo());
                    employmentTemp.setPosition(previousEmploymentObj.getPosition());
                    employmentTemp.setReasonForSeparation(previousEmploymentObj.getReasonForSeparation());
                    employmentTemp.setRemark(previousEmploymentObj.getRemark());
                    employmentTemp.setResponsibilities(previousEmploymentObj.getResponsibilities());
                    employmentTemp.setServiceType(previousEmploymentObj.getServiceType());
                    employmentTemp.setToDate(previousEmploymentObj.getToDate());
                    employmentTemp.setWebSiteUrl(previousEmploymentObj.getWebSiteUrl());

                    EmpPreviousEmploymentTemp ep = empPreviousEmploymentTempRepository.save(employmentTemp);
                    if (ep != null) {
                        res.setObj(ep);
                        res.setStatus("SUCCESS");
                    }
                }

                auditTrailService.saveAuditTrailData("Employee Previous Employment Information", "Save", "Admin",
                        NotificationModule.EMPLOYEE_Previous_Employment, NotificationAction.ADD, "/save", userId);
            } else {

                if (um.getRoleMasterId().getIsAdmin()) {
                    EmpPreviousEmployment em = empPreviousEmploymentRepository.save(previousEmploymentObj);
                    if (em != null) {
                        EmpPreviousEmployment empObj = (EmpPreviousEmployment) em;
                        res.setObj(empObj);
                        res.setStatus("UPDATE");
                    }
                } else {
                    if (request.getParameter("isTempPreviousEmployeementDetails").trim().isEmpty()) {
                        EmpPreviousEmployment em = empPreviousEmploymentRepository
                                .findById(previousEmploymentObj.getId()).get();

                        EmpPreviousEmploymentTemp employmentTemp = new EmpPreviousEmploymentTemp();

                        if (empPreviousEmploymentTempRepository
                                .findByempPreviousEmploymentMstIdAndIsApprovedFalse(previousEmploymentObj.getId())
                                .isPresent()) {
                            employmentTemp = empPreviousEmploymentTempRepository
                                    .findByempPreviousEmploymentMstIdAndIsApprovedFalse(previousEmploymentObj.getId())
                                    .get();
                        }
                        employmentTemp.setIsEditedRecord(true);
                        employmentTemp.setIsDeletedRecord(false);
                        employmentTemp.setEmp(em.getEmp());
                        employmentTemp.setEmpPreviousEmploymentMstId(em.getId());

                        if (!Objects.equal(em.getAddress() != null ? em.getAddress() : "",
                                previousEmploymentObj.getAddress() != null ? previousEmploymentObj.getAddress() : ""))
                            employmentTemp.setAddress(previousEmploymentObj.getAddress());
                        else
                            employmentTemp.setAddress(null);

                        if (!Objects.equal(em.getCompanyName() != null ? em.getCompanyName() : "",
                                previousEmploymentObj.getCompanyName() != null ? previousEmploymentObj.getCompanyName()
                                        : ""))
                            employmentTemp.setCompanyName(previousEmploymentObj.getCompanyName());
                        else
                            employmentTemp.setCompanyName(null);

                        if (!Objects.equal(em.getEmail() != null ? em.getEmail() : "",
                                previousEmploymentObj.getEmail() != null ? previousEmploymentObj.getEmail() : ""))
                            employmentTemp.setEmail(previousEmploymentObj.getEmail());

                        if (!Objects.equal(em.getFromDate() != null ? em.getFromDate() : "",
                                previousEmploymentObj.getFromDate() != null ? previousEmploymentObj.getFromDate() : ""))
                            employmentTemp.setFromDate(previousEmploymentObj.getFromDate());
                        else
                            employmentTemp.setFromDate(null);

                        if (!Objects.equal(em.getHrContactPerson() != null ? em.getHrContactPerson() : "",
                                previousEmploymentObj.getHrContactPerson() != null
                                        ? previousEmploymentObj.getHrContactPerson()
                                        : ""))
                            employmentTemp.setHrContactPerson(previousEmploymentObj.getHrContactPerson());
                        else
                            employmentTemp.setHrContactPerson(null);

                        if (!Objects.equal(em.getLastCTC() != null ? em.getLastCTC() : "",
                                previousEmploymentObj.getLastCTC() != null ? previousEmploymentObj.getLastCTC() : ""))
                            employmentTemp.setLastCTC(previousEmploymentObj.getLastCTC());
                        else
                            employmentTemp.setLastCTC(null);

                        if (!Objects.equal(em.getMobileNo() != null ? em.getMobileNo() : "",
                                previousEmploymentObj.getMobileNo() != null ? previousEmploymentObj.getMobileNo() : ""))
                            employmentTemp.setMobileNo(previousEmploymentObj.getMobileNo());
                        else
                            employmentTemp.setMobileNo(null);

                        if (!Objects.equal(em.getPhoneNo() != null ? em.getPhoneNo() : "",
                                previousEmploymentObj.getPhoneNo() != null ? previousEmploymentObj.getPhoneNo() : ""))
                            employmentTemp.setPhoneNo(previousEmploymentObj.getPhoneNo());
                        else
                            employmentTemp.setPhoneNo(null);

                        if (!Objects.equal(em.getPosition() != null ? em.getPosition() : "",
                                previousEmploymentObj.getPosition() != null ? previousEmploymentObj.getPosition() : ""))
                            employmentTemp.setPosition(previousEmploymentObj.getPosition());
                        else
                            employmentTemp.setPosition(null);

                        if (!Objects.equal(em.getReasonForSeparation() != null ? em.getReasonForSeparation() : "",
                                previousEmploymentObj.getReasonForSeparation() != null
                                        ? previousEmploymentObj.getReasonForSeparation()
                                        : ""))
                            employmentTemp.setReasonForSeparation(previousEmploymentObj.getReasonForSeparation());
                        else
                            employmentTemp.setReasonForSeparation(null);

                        if (!Objects.equal(em.getRemark() != null ? em.getRemark() : "",
                                previousEmploymentObj.getRemark() != null ? previousEmploymentObj.getRemark() : ""))
                            employmentTemp.setRemark(previousEmploymentObj.getRemark());
                        else
                            employmentTemp.setRemark(null);

                        if (!Objects.equal(em.getResponsibilities() != null ? em.getResponsibilities() : "",
                                previousEmploymentObj.getResponsibilities() != null
                                        ? previousEmploymentObj.getResponsibilities()
                                        : ""))
                            employmentTemp.setResponsibilities(previousEmploymentObj.getResponsibilities());

                        if (!Objects.equal(em.getServiceType() != null ? em.getServiceType() : "",
                                previousEmploymentObj.getServiceType() != null ? previousEmploymentObj.getServiceType()
                                        : ""))
                            employmentTemp.setServiceType(previousEmploymentObj.getServiceType());

                        if (!Objects.equal(em.getToDate() != null ? em.getToDate() : "",
                                previousEmploymentObj.getToDate() != null ? previousEmploymentObj.getToDate() : ""))
                            employmentTemp.setToDate(previousEmploymentObj.getToDate());

                        if (!Objects.equal(em.getWebSiteUrl() != null ? em.getWebSiteUrl() : "",
                                previousEmploymentObj.getWebSiteUrl() != null ? previousEmploymentObj.getWebSiteUrl()
                                        : ""))
                            employmentTemp.setWebSiteUrl(previousEmploymentObj.getWebSiteUrl());

                        EmpPreviousEmploymentTemp ep = empPreviousEmploymentTempRepository.save(employmentTemp);
                        if (ep != null) {
                            res.setObj(ep);
                            res.setStatus("UPDATE");
                        }

                    } else {
                        EmpPreviousEmploymentTemp employmentTemp = new EmpPreviousEmploymentTemp();

                        if (empPreviousEmploymentTempRepository.findById(previousEmploymentObj.getId()).isPresent()) {
                            employmentTemp = empPreviousEmploymentTempRepository.findById(previousEmploymentObj.getId())
                                    .get();
                        }
                        employmentTemp.setAddress(previousEmploymentObj.getAddress());
                        employmentTemp.setCompanyName(previousEmploymentObj.getCompanyName());
                        employmentTemp.setEmail(previousEmploymentObj.getEmail());
                        employmentTemp.setEmp(previousEmploymentObj.getEmp());
                        employmentTemp.setFromDate(previousEmploymentObj.getFromDate());
                        employmentTemp.setHrContactPerson(previousEmploymentObj.getHrContactPerson());
                        employmentTemp.setLastCTC(previousEmploymentObj.getLastCTC());
                        employmentTemp.setMobileNo(previousEmploymentObj.getMobileNo());
                        employmentTemp.setPhoneNo(previousEmploymentObj.getPhoneNo());
                        employmentTemp.setPosition(previousEmploymentObj.getPosition());
                        employmentTemp.setReasonForSeparation(previousEmploymentObj.getReasonForSeparation());
                        employmentTemp.setRemark(previousEmploymentObj.getRemark());
                        employmentTemp.setResponsibilities(previousEmploymentObj.getResponsibilities());
                        employmentTemp.setServiceType(previousEmploymentObj.getServiceType());
                        employmentTemp.setToDate(previousEmploymentObj.getToDate());
                        employmentTemp.setWebSiteUrl(previousEmploymentObj.getWebSiteUrl());

                        EmpPreviousEmploymentTemp ep = empPreviousEmploymentTempRepository.save(employmentTemp);
                        if (ep != null) {
                            res.setObj(ep);
                            res.setStatus("UPDATE");
                        }
                    }
                }

                auditTrailService.saveAuditTrailData("Employee Previous Employment Information", "Update", "Admin",
                        NotificationModule.EMPLOYEE_Previous_Employment, NotificationAction.UPDATE, "/save", userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update employee Previous Employment information, " + e.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "/editEmpPreviousEmployment")
    public @ResponseBody JsonResponse editEmpPreviousEmployment(@RequestParam("id") Long id, HttpSession session,
                                                                @RequestParam("isTempData") Boolean isTempData) {
        logger.info("employee Previous Employment id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        if (um.getRoleMasterId().getIsAdmin()) {
            Optional<EmpPreviousEmployment> empPreviousEmployment = empPreviousEmploymentRepository.findById(id);
            if (empPreviousEmployment.isPresent()) {

                EmpPreviousEmployment previousEmploymentObj = (EmpPreviousEmployment) empPreviousEmployment.get();
                res.setObj(previousEmploymentObj);

                res.setStatus("SUCCESS");
            }
        } else {
            if (!isTempData) {
                Optional<EmpPreviousEmployment> empPreviousEmployment = empPreviousEmploymentRepository.findById(id);
                if (empPreviousEmployment.isPresent()) {

                    EmpPreviousEmployment previousEmploymentObj = (EmpPreviousEmployment) empPreviousEmployment.get();
                    res.setObj(previousEmploymentObj);

                    res.setStatus("SUCCESS");
                }
            } else {
                Optional<EmpPreviousEmploymentTemp> empPreEmployment = empPreviousEmploymentTempRepository.findById(id);

                if (empPreEmployment.isPresent()) {
                    res.setObj(empPreEmployment.get());

                    res.setStatus("SUCCESS");
                }
            }
        }

        return res;
    }

    @RequestMapping(value = "/getPreviousEmploymentList/{id}")
    public @ResponseBody JsonResponse getPreviousEmploymentList(@PathVariable("id") Long id, Model model,
                                                                HttpServletRequest request, HttpSession session) {

        JsonResponse res = new JsonResponse();
        List<EmpPreviousEmployment> getPreviousEmploymentList = empPreviousEmploymentRepository.findByEmpId(id);

        List<Object> emppreempList = new ArrayList<Object>();
        emppreempList.addAll(getPreviousEmploymentList);

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        if (um.getRoleMasterId().getIsAdmin()) {
            if (emppreempList.size() > 0) {
                res.setStatus("success");
                res.setObjList(emppreempList);
            }
        }else {
            if (emppreempList.size() > 0) {
                res.setStatus("success");
                res.setObjList(emppreempList);
            } else {
                List<EmpPreviousEmploymentTemp> employmentTemps = empPreviousEmploymentTempRepository
                        .findAllByEmpIdAndIsApprovedFalseAndIsDeletedRecordFalseAndIsEditedRecordFalse(id);

                emppreempList.addAll(employmentTemps);
                if (emppreempList.size() > 0) {
                    res.setStatus("success");
                    res.setObjList(emppreempList);
                    res.setTempTableData(true);
                }
            }
        }
        return res;
    }

    @GetMapping(value = "/deletPreviousEmployment/{id}")
    public @ResponseBody JsonResponse deletPreviousEmployment(@PathVariable("id") Long id, Model model,
                                                              HttpServletRequest request, HttpSession session, @RequestParam("isTempData") Boolean isTempData) {
        logger.info("EmployeeController.deletPreviousEmployment");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            if (um != null) {

                if (um.getRoleMasterId().getIsAdmin()) {
                    Optional<EmpPreviousEmployment> empPreviousEmployment = empPreviousEmploymentRepository
                            .findById(id);
                    if (empPreviousEmployment.isPresent()) {

                        List<EmpPreviousEmployment> getPreviousEmploymentList = empPreviousEmploymentRepository
                                .findByEmpId(empPreviousEmployment.get().getEmp().getId());
                        List<Object> emppreempList = new ArrayList<Object>();
                        emppreempList.addAll(getPreviousEmploymentList);
                        if (emppreempList.size() > 0) {
                            res.setObjList(emppreempList);
                        }

                        empPreviousEmploymentRepository.deleteById(id);

                        if (empPreviousEmploymentTempRepository.findByempPreviousEmploymentMstIdAndIsApprovedFalse(id)
                                .isPresent()) {
                            EmpPreviousEmploymentTemp employmentTemp = empPreviousEmploymentTempRepository
                                    .findByempPreviousEmploymentMstIdAndIsApprovedFalse(id).get();

                            empPreviousEmploymentTempRepository.deleteById(employmentTemp.getId());
                        }

                        res.setStatus("SUCCESS");
                    } else {
                        res.setStatus("FAIL");
                    }
                } else {
                    if (isTempData) {
                        empPreviousEmploymentTempRepository.deleteById(id);
                        res.setStatus("SUCCESS");
                    } else {

                        Optional<EmpPreviousEmployment> empPreviousEmployment = empPreviousEmploymentRepository
                                .findById(id);
                        if (empPreviousEmployment.isPresent()) {
                            EmpPreviousEmploymentTemp employmentTemp = new EmpPreviousEmploymentTemp();

                            if (empPreviousEmploymentTempRepository
                                    .findByempPreviousEmploymentMstIdAndIsApprovedFalse(id).isPresent()) {
                                employmentTemp = empPreviousEmploymentTempRepository
                                        .findByempPreviousEmploymentMstIdAndIsApprovedFalse(id).get();
                            }
                            employmentTemp.setIsEditedRecord(false);
                            employmentTemp.setIsDeletedRecord(true);
                            employmentTemp.setEmp(empPreviousEmployment.get().getEmp());
                            employmentTemp.setEmpPreviousEmploymentMstId(id);

                            employmentTemp.setAddress(null);
                            employmentTemp.setCompanyName(null);
                            employmentTemp.setEmail(null);
                            employmentTemp.setFromDate(null);
                            employmentTemp.setHrContactPerson(null);
                            employmentTemp.setLastCTC(null);
                            employmentTemp.setMobileNo(null);
                            employmentTemp.setPhoneNo(null);
                            employmentTemp.setPosition(null);
                            employmentTemp.setReasonForSeparation(null);
                            employmentTemp.setRemark(null);
                            employmentTemp.setResponsibilities(null);
                            employmentTemp.setServiceType(null);
                            employmentTemp.setToDate(null);
                            employmentTemp.setWebSiteUrl(null);

                            EmpPreviousEmploymentTemp ep = empPreviousEmploymentTempRepository.save(employmentTemp);


//							For save Deleted record of Previous Employment


//							EmpPreviousEmploymentHistory empPreviousEmploymentHistory = new EmpPreviousEmploymentHistory();
//
//							empPreviousEmploymentHistory.setEmp(empPreviousEmployment.get().getEmp());
//							empPreviousEmploymentHistory.setEmpPreviousEmploymentMstId(employmentTemp.getId());
//							empPreviousEmploymentHistory.setIsEditedRecord(false);
//							empPreviousEmploymentHistory.setIsDeletedRecord(true);
//							empPreviousEmploymentHistory.setAddress(empPreviousEmployment.get().getAddress());
//							empPreviousEmploymentHistory.setCompanyName(empPreviousEmployment.get().getCompanyName());
//							empPreviousEmploymentHistory.setEmail(empPreviousEmployment.get().getEmail());
//							empPreviousEmploymentHistory.setFromDate(empPreviousEmployment.get().getFromDate());
//							empPreviousEmploymentHistory.setHrContactPerson(empPreviousEmployment.get().getHrContactPerson());
//							empPreviousEmploymentHistory.setLastCTC(empPreviousEmployment.get().getLastCTC());
//							empPreviousEmploymentHistory.setMobileNo(empPreviousEmployment.get().getMobileNo());
//							empPreviousEmploymentHistory.setPhoneNo(empPreviousEmployment.get().getPhoneNo());
//							empPreviousEmploymentHistory.setPosition(empPreviousEmployment.get().getPosition());
//							empPreviousEmploymentHistory.setReasonForSeparation(empPreviousEmployment.get().getReasonForSeparation());
//							empPreviousEmploymentHistory.setRemark(empPreviousEmployment.get().getRemark());
//							empPreviousEmploymentHistory.setResponsibilities(empPreviousEmployment.get().getResponsibilities());
//							empPreviousEmploymentHistory.setServiceType(empPreviousEmployment.get().getServiceType());
//							empPreviousEmploymentHistory.setToDate(empPreviousEmployment.get().getToDate());
//							empPreviousEmploymentHistory.setWebSiteUrl(empPreviousEmployment.get().getWebSiteUrl());
//
//							empPreviousEmploymentHistoryRepository.save(empPreviousEmploymentHistory);




                            if (ep != null) {
                                res.setObj(ep);
                                res.setStatus("SUCCESS");
                            } else {
                                res.setStatus("FAIL");
                            }
                        } else {
                            res.setStatus("FAIL");
                        }
                    }
                }

                auditTrailService.saveAuditTrailData("Employee Previous Employment Information", "Delete", "Admin",
                        NotificationModule.EMPLOYEE_Previous_Employment, NotificationAction.DELETE,
                        "/deleteEmpEmergency", userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus("FAIL");
        }

        return res;
    }
    // end previous employment

    // start attachments

    @RequestMapping(value = "/saveEmpAttachements")
    public @ResponseBody JsonResponse saveEmpAttachements(HttpServletRequest request, HttpSession session, Model model,
                                                          @ModelAttribute("attachmentsObj") EmpAttachmentsDtl attachmentsObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            EmpAttachmentsValidator validatior = new EmpAttachmentsValidator();
            validatior.validate(attachmentsObj, result);
            if (result.hasErrors()) {
                model.addAttribute("attachmentsObj", attachmentsObj);

                res.setStatus("FAIL");

                Map<String, String> errors = new HashMap<String, String>();
                errors = result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                res.setResult(errors);
                return res;
            }

            if (null != attachmentsObj.getDocumentAttchFile()
                    && !"".equals(attachmentsObj.getDocumentAttchFile().getOriginalFilename())) {
                String name = attachmentsObj.getDocumentAttchFile().getOriginalFilename();
                FileMaster fileMaster = commonUtility.saveFileObject(name, attachmentsObj.getDocumentAttchFile(),
                        CommonConstant.EMP_FILES, companyId, companyBranchId);

                System.out.println("Path-------------->>>>>>>>>" + CommonConstant.EMP_FILES);


                if (fileMaster != null) {
                    attachmentsObj.setDocumentAttch(fileMaster);
                }
            } else {
                attachmentsObj.setDocumentAttch(attachmentsObj.getDocumentAttch());
            }

            if (attachmentsObj.getId() == null) {

                if (um.getRoleMasterId().getIsAdmin()) {
                    EmpAttachmentsDtl em = empAttachmentsRepository.save(attachmentsObj);
                    if (em != null) {
                        EmpAttachmentsDtl empObj = (EmpAttachmentsDtl) em;
                        res.setObj(empObj.getEmp().getId());
                        res.setStatus("SUCCESS");
                    }
                } else {

                    EmpAttachmentsDtlTemp attachmentsDtlTemp = new EmpAttachmentsDtlTemp();

                    attachmentsDtlTemp.setDocumentAttch(attachmentsObj.getDocumentAttch());
                    attachmentsDtlTemp.setTitleOfDocumentAtc(attachmentsObj.getTitleOfDocumentAtc());
                    attachmentsDtlTemp.setDocumentCategoryAtc(attachmentsObj.getDocumentCategoryAtc());
                    attachmentsDtlTemp.setDocumentDescriptionAtc(attachmentsObj.getDocumentDescriptionAtc());
                    attachmentsDtlTemp.setEmp(attachmentsObj.getEmp());

                    EmpAttachmentsDtlTemp em = empAttachmentsTempRepository.save(attachmentsDtlTemp);

                    if (em != null) {
                        res.setObj(em);
                        res.setStatus("SUCCESS");
                    }

                }

                auditTrailService.saveAuditTrailData("Employee Attachments", "Save", "Admin",
                        NotificationModule.EMPLOYEE_Attachments, NotificationAction.ADD, "/save", userId);
            } else {

                if (null != attachmentsObj.getDocumentAttchFile()
                        && !"".equals(attachmentsObj.getDocumentAttchFile().getOriginalFilename())) {
                    String name = attachmentsObj.getDocumentAttchFile().getOriginalFilename();
                    FileMaster fileMaster = commonUtility.saveFileObject(name, attachmentsObj.getDocumentAttchFile(),
                            CommonConstant.EMP_FILES, companyId, companyBranchId);
                    System.out.println("Path-------------->>>>>>>>> " + CommonConstant.EMP_FILES);
                    if (fileMaster != null) {
                        attachmentsObj.setDocumentAttch(fileMaster);
                    }
                } else {
                    attachmentsObj.setDocumentAttch(attachmentsObj.getDocumentAttch());
                }

                if (attachmentsObj.getDocumentAttch() == null) {

                    // attachmentsObj
                    Optional<EmpAttachmentsDtl> em = empAttachmentsRepository.findById(attachmentsObj.getId());
                    if (em.isPresent()) {
                        Optional<FileMaster> fm = fileMasterRepository.findById(em.get().getDocumentAttch().getId());
                        if (fm.isPresent()) {
                            attachmentsObj.setDocumentAttch(fm.get());
                        }
                    }

                }

                if (um.getRoleMasterId().getIsAdmin()) {

                    EmpAttachmentsDtl em = empAttachmentsRepository.save(attachmentsObj);
                    if (em != null) {
                        EmpAttachmentsDtl empObj = (EmpAttachmentsDtl) em;
                        res.setObj(empObj.getEmp().getId());
                        res.setStatus("UPDATE");
                    }

                } else {
                    if (request.getParameter("isTempAttachmentsDetails").trim().isEmpty()) {

                        EmpAttachmentsDtl empAttachmentsDtl = empAttachmentsRepository.findById(attachmentsObj.getId())
                                .get();

                        EmpAttachmentsDtlTemp attachmentsDtlTemp = new EmpAttachmentsDtlTemp();

                        if (empAttachmentsTempRepository
                                .findByEmpAttachmentsDtlMstIdAndIsApprovedFalse(attachmentsObj.getId()).isPresent()) {
                            attachmentsDtlTemp = empAttachmentsTempRepository
                                    .findByEmpAttachmentsDtlMstIdAndIsApprovedFalse(attachmentsObj.getId()).get();
                        }

                        attachmentsDtlTemp.setDocumentAttch(attachmentsObj.getDocumentAttch());
                        attachmentsDtlTemp.setEmp(attachmentsObj.getEmp());
                        attachmentsDtlTemp.setEmpAttachmentsDtlMstId(attachmentsObj.getId());
                        attachmentsDtlTemp.setIsEditedRecord(true);
                        attachmentsDtlTemp.setIsDeletedRecord(false);

                        if (!Objects.equal(
                                empAttachmentsDtl.getDocumentCategoryAtc() == null ? ""
                                        : empAttachmentsDtl.getDocumentCategoryAtc(),
                                attachmentsObj.getDocumentCategoryAtc()))
                            attachmentsDtlTemp.setDocumentCategoryAtc(attachmentsObj.getDocumentCategoryAtc());

                        if (!Objects.equal(
                                empAttachmentsDtl.getDocumentDescriptionAtc() == null ? ""
                                        : empAttachmentsDtl.getDocumentDescriptionAtc(),
                                attachmentsObj.getDocumentDescriptionAtc()))
                            attachmentsDtlTemp.setDocumentDescriptionAtc(attachmentsObj.getDocumentDescriptionAtc());

                        if (!Objects.equal(
                                empAttachmentsDtl.getTitleOfDocumentAtc() == null ? ""
                                        : empAttachmentsDtl.getTitleOfDocumentAtc(),
                                attachmentsObj.getTitleOfDocumentAtc()))
                            attachmentsDtlTemp.setTitleOfDocumentAtc(attachmentsObj.getTitleOfDocumentAtc());

                        EmpAttachmentsDtlTemp em = empAttachmentsTempRepository.save(attachmentsDtlTemp);

                        if (em != null) {
                            res.setObj(em);
                            res.setStatus("UPDATE");
                        }

                    } else {

                        EmpAttachmentsDtlTemp attachmentsDtlTemp = new EmpAttachmentsDtlTemp();

                        if (empAttachmentsTempRepository.findById(attachmentsObj.getId()).isPresent()) {
                            attachmentsDtlTemp = empAttachmentsTempRepository.findById(attachmentsObj.getId()).get();
                        }

                        // attachmentsDtlTemp.setEmpAttachmentsDtlMstId(attachmentsObj.getId());

                        attachmentsDtlTemp.setDocumentAttch(attachmentsObj.getDocumentAttch());
                        attachmentsDtlTemp.setTitleOfDocumentAtc(attachmentsObj.getTitleOfDocumentAtc());
                        attachmentsDtlTemp.setDocumentCategoryAtc(attachmentsObj.getDocumentCategoryAtc());
                        attachmentsDtlTemp.setDocumentDescriptionAtc(attachmentsObj.getDocumentDescriptionAtc());
                        attachmentsDtlTemp.setEmp(attachmentsObj.getEmp());

                        EmpAttachmentsDtlTemp em = empAttachmentsTempRepository.save(attachmentsDtlTemp);

                        if (em != null) {
                            res.setObj(em);
                            res.setStatus("UPDATE");
                        }

                    }

                }
                auditTrailService.saveAuditTrailData("Employee Attachments", "Update", "Admin",
                        NotificationModule.EMPLOYEE_Attachments, NotificationAction.UPDATE, "/save", userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update Employee Attachments, " + e.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "/editEmpAttachements")
    public @ResponseBody JsonResponse editEmpAttachements(@RequestParam("id") Long id, HttpSession session,
                                                          @RequestParam("isTempData") Boolean isTempData) throws IOException {
        logger.info("employee Attachements id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        if (um.getRoleMasterId().getIsAdmin()) {
            Optional<EmpAttachmentsDtl> empAttachmentsDtl = empAttachmentsRepository.findById(id);
            if (empAttachmentsDtl.isPresent()) {

                if (empAttachmentsDtl.get().getDocumentAttch() != null) {
                    File file = new File(environment.getProperty("file.repository.hrms.path") + companyId
                            + File.separator + companyBranchId + File.separator + CommonConstant.EMP_FILES
                            + File.separator + empAttachmentsDtl.get().getDocumentAttch().getFileName());
                    if (file != null && file.exists()) {
                        byte[] byteArry = CommonUtility.toByteArray(file);
                        String userProfileImg = javax.xml.bind.DatatypeConverter.printBase64Binary(byteArry);
                        empAttachmentsDtl.get().setEmpDocumentAttchFile(userProfileImg);
                    }
                }

                EmpAttachmentsDtl attachmentsObj = (EmpAttachmentsDtl) empAttachmentsDtl.get();
                res.setObj(attachmentsObj);

                res.setStatus("SUCCESS");
            }
        } else {
            if (isTempData) {
                EmpAttachmentsDtlTemp empAttachmentsDtlTemp = empAttachmentsTempRepository.findById(id).orElse(null);

                if (empAttachmentsDtlTemp != null) {
                    res.setTempTableData(true);

                    res.setObj(empAttachmentsDtlTemp);
                    res.setStatus("SUCCESS");
                } else {
                    res.setStatus("FAIL");
                    return res;
                }

            } else {

                Optional<EmpAttachmentsDtl> empAttachmentsDtl = empAttachmentsRepository.findById(id);
                if (empAttachmentsDtl.isPresent()) {

                    if (empAttachmentsDtl.get().getDocumentAttch() != null) {
                        File file = new File(environment.getProperty("file.repository.hrms.path") + companyId
                                + File.separator + companyBranchId + File.separator + CommonConstant.EMP_FILES
                                + File.separator + empAttachmentsDtl.get().getDocumentAttch().getFileName());
                        if (file != null && file.exists()) {
                            byte[] byteArry = CommonUtility.toByteArray(file);
                            String userProfileImg = javax.xml.bind.DatatypeConverter.printBase64Binary(byteArry);
                            empAttachmentsDtl.get().setEmpDocumentAttchFile(userProfileImg);
                        }
                    }

                    EmpAttachmentsDtl attachmentsObj = (EmpAttachmentsDtl) empAttachmentsDtl.get();
                    res.setObj(attachmentsObj);

                    res.setStatus("SUCCESS");
                }

            }
        }

        return res;
    }

    @RequestMapping(value = "/getEmpAttachementsList/{id}")
    public @ResponseBody JsonResponse getEmpAttachementsList(@PathVariable("id") Long id, Model model,
                                                             HttpServletRequest request, HttpSession session) {

        JsonResponse res = new JsonResponse();
        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        List<EmpAttachmentsDtl> getEmpAttachementsList = empAttachmentsRepository.findByEmpId(id);

        List<Object> empattchList = new ArrayList<Object>();
        empattchList.addAll(getEmpAttachementsList);
        if (empattchList.size() > 0) {
            res.setStatus("success");
            res.setObjList(empattchList);
        } else {
            if (!um.getRoleMasterId().getIsAdmin()) {

                List<EmpAttachmentsDtlTemp> attachmentsDtlTemps = empAttachmentsTempRepository
                        .findAllByEmpIdAndIsApprovedFalse(id);
                if (attachmentsDtlTemps.size() > 0) {
                    empattchList.addAll(attachmentsDtlTemps);
                    res.setStatus("success");
                    res.setObjList(empattchList);
                    res.setTempTableData(true);
                }
            }
        }

        return res;
    }

    @GetMapping(value = "/downloadFile-{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response, HttpServletRequest request,
                             HttpSession session) {
        try {
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");
            FileMaster fileMaster1 = fileMasterRepository.findById(id).orElse(null);


            File file = new File(
                    environment.getProperty("file.repository.hrms.path").concat(companyId.toString() + File.separator)
                            .concat(companyBranchId.toString() + File.separator).concat(CommonConstant.EMP_FILES) + "/"
                            + fileMaster1.getFileName());

            if (file.exists()) {
                byte[] fileInByteArray = toByteArray(file);
                if (fileInByteArray != null) {
                    response.setContentType(new MimetypesFileTypeMap().getContentType(environment
                            .getProperty("file.repository.hrms.path").concat(companyId.toString() + File.separator)
                            .concat(companyBranchId.toString() + File.separator).concat(CommonConstant.CLAIM_FILES) + "/"
                            + fileMaster1.getFileName()));
                    response.setContentLength(fileInByteArray.length);
                    response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + fileMaster1.getActualFileName());
                    response.getOutputStream().write(fileInByteArray);
                    response.getOutputStream().flush();
                }
            } else {
//				byte[] fileInByteArray = ReportUtil.emptyPdfResponse(genFile.getFileName(),"Error: File not found.");
//				ReportUtil.getPdfReportResponse(fileInByteArray, response, genFile.getFileName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private static byte[] toByteArray(File file) throws IOException {
        byte[] bytesArray = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); // read file into bytes[]
        fis.close();
        return bytesArray;
    }


    @GetMapping(value = "/deletEmpAttachements/{id}")
    public @ResponseBody JsonResponse deletEmpAttachements(@PathVariable("id") Long id, Model model,
                                                           HttpServletRequest request, HttpSession session, @RequestParam("isTempData") Boolean isTempData) {
        logger.info("EmployeeController.deletEmpAttachements");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            if (um != null) {
                Optional<EmpAttachmentsDtl> empAttachmentsDtl = empAttachmentsRepository.findById(id);

                if (um.getRoleMasterId().getIsAdmin()) {

                    if (empAttachmentsDtl.isPresent()) {
                        List<EmpAttachmentsDtl> getEmpAttachementsList = empAttachmentsRepository
                                .findByEmpId(empAttachmentsDtl.get().getEmp().getId());
                        List<Object> empattachmntList = new ArrayList<Object>();
                        empattachmntList.addAll(getEmpAttachementsList);
                        if (empattachmntList.size() > 0) {
                            res.setObjList(empattachmntList);
                        }

                        empAttachmentsRepository.deleteById(id);
                        if (empAttachmentsTempRepository.findByEmpAttachmentsDtlMstIdAndIsApprovedFalse(id)
                                .isPresent()) {
                            EmpAttachmentsDtlTemp attachmentsDtlTemp = empAttachmentsTempRepository
                                    .findByEmpAttachmentsDtlMstIdAndIsApprovedFalse(id).get();
                            empAttachmentsTempRepository.deleteById(attachmentsDtlTemp.getId());
                            res.setStatus("SUCCESS");
                        }

                        res.setStatus("SUCCESS");
                    } else {
                        res.setStatus("FAIL");
                    }
                } else {
                    if (isTempData) {

                        empAttachmentsTempRepository.deleteById(id);
                        res.setStatus("SUCCESS");
                    } else {
                        EmpAttachmentsDtlTemp attachmentsDtlTemp = new EmpAttachmentsDtlTemp();

                        if (empAttachmentsTempRepository.findByEmpAttachmentsDtlMstIdAndIsApprovedFalse(id)
                                .isPresent()) {
                            attachmentsDtlTemp = empAttachmentsTempRepository
                                    .findByEmpAttachmentsDtlMstIdAndIsApprovedFalse(id).get();
                        }
                        attachmentsDtlTemp.setIsDeletedRecord(true);
                        attachmentsDtlTemp.setEmpAttachmentsDtlMstId(id);
                        attachmentsDtlTemp.setIsEditedRecord(false);

                        attachmentsDtlTemp.setDocumentCategoryAtc(null);
                        attachmentsDtlTemp.setDocumentDescriptionAtc(null);
                        attachmentsDtlTemp.setTitleOfDocumentAtc(null);
                        attachmentsDtlTemp.setDocumentAttch(null);

                        EmpAttachmentsDtlTemp em = empAttachmentsTempRepository.save(attachmentsDtlTemp);

                        if (em != null) {
                            res.setStatus("SUCCESS");
                        }
                    }
                }

                auditTrailService.saveAuditTrailData("Employee Attachments", "Delete", "Admin",
                        NotificationModule.EMPLOYEE_Attachments, NotificationAction.DELETE, "/deletEmpAttachements",
                        userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus("FAIL");
        }

        return res;
    }

    // end attachments

    @RequestMapping(value = "/getCompBranch")
    public @ResponseBody JsonResponse getCompBranch(@RequestParam("id") Long company, HttpSession session) {
        logger.info("getCompBranch id " + company);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }
        List<CompanyBranchMaster> companyBranchList = companyBranchMasterRepository.findByCompanyIdAndIsDelete(company,
                false);

        List<Object> compBranchMstList = new ArrayList<Object>();
        compBranchMstList.addAll(companyBranchList);
        if (companyBranchList.size() > 0) {
            res.setStatus("success");
            res.setObjList(compBranchMstList);
        }

        return res;
    }

    // Controller endpoint for fetching worklocation data
    @RequestMapping(value = "/getWorklocation")
    public @ResponseBody JsonResponse getWorklocation(@RequestParam("company") Long companyId,
                                                      @RequestParam("branchid") Long companyBranchId) {
        // logger.info("getWorklocation id " + branchId);
        logger.info("getWorklocation companyId: " + companyId + ", companyBranchId: " + companyBranchId);

        JsonResponse res = new JsonResponse();

        // UserMaster um = (UserMaster) session.getAttribute("usermaster");
        // Long companyId = (Long) session.getAttribute("companyId");
        // Long companyBranchId = (Long) session.getAttribute("companyBranchId");

//		    if (um == null || companyId == null || companyBranchId == null) {
//		        res.setStatus("FAIL");
//		        return res;
//		    }

        // Assuming you have a service method to retrieve worklocation data based on
        // branchId
        List<Object> worklocationData = jobRepo.findWorklocation(companyId, companyBranchId);

        // List<Object> rm = jobRepo.findWorklocation(companyId,companyBranchId);
        // System.out.println(rm);

        // model.addAttribute("rm", rm);

        List<Object> worklocationList = new ArrayList<>();
        worklocationList.addAll(worklocationData);

        if (!worklocationList.isEmpty()) {
            res.setStatus("success");
            res.setObjList(worklocationList);
        } else {
            res.setStatus("error");
            // You can provide additional error information if needed
        }

        return res;
    }

    @PostMapping(value = "/saveJobDetails")
    public @ResponseBody JsonResponse saveJobDetails(HttpServletRequest request, HttpSession session, Model model,
                                                     @ModelAttribute("jobObj") Job jobObj, BindingResult result) {
        logger.info("saveJobDetails");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            String contractDate = request.getParameter("contractDateStr");
            if (StringUtil.isNotEmpty(contractDate)) {
                jobObj.setContractDate(DateUtil.convertStringToDate(contractDate, DateUtil.IST_DATE_FORMATE));
            }

            String confirmationDate = request.getParameter("confirmationDateStr");
            if (StringUtil.isNotEmpty(confirmationDate)) {
                jobObj.setConfirmationDate(DateUtil.convertStringToDate(confirmationDate, DateUtil.IST_DATE_FORMATE));
            }

            String retirementDate = request.getParameter("retirementDateStr");
            if (StringUtil.isNotEmpty(retirementDate)) {
                jobObj.setRetirementDate(DateUtil.convertStringToDate(retirementDate, DateUtil.IST_DATE_FORMATE));
            }

            String insuranceWithEffectiveFrom = request.getParameter("insuranceWithEffectiveFromStr");
            if (StringUtil.isNotEmpty(insuranceWithEffectiveFrom)) {
                jobObj.setInsuranceWithEffectiveFrom(
                        DateUtil.convertStringToDate(insuranceWithEffectiveFrom, DateUtil.IST_DATE_FORMATE));
            }

            String postingDate = request.getParameter("postingDateStr");
            if (StringUtil.isNotEmpty(postingDate)) {
                jobObj.setPostingDate(DateUtil.convertStringToDate(postingDate, DateUtil.IST_DATE_FORMATE));
            }

            String postingOrderDate = request.getParameter("postingOrderDateStr");
            if (StringUtil.isNotEmpty(postingOrderDate)) {
                jobObj.setPostingOrderDate(DateUtil.convertStringToDate(postingOrderDate, DateUtil.IST_DATE_FORMATE));
            }

            String weeklyOffWithEffectiveFrom = request.getParameter("weeklyOffWithEffectiveFromStr");
            if (StringUtil.isNotEmpty(weeklyOffWithEffectiveFrom)) {
                jobObj.setWeeklyOffWithEffectiveFrom(
                        DateUtil.convertStringToDate(weeklyOffWithEffectiveFrom, DateUtil.IST_DATE_FORMATE));
            }

            String traineeFromDate = request.getParameter("traineeFromDateStr");
            if (StringUtil.isNotEmpty(traineeFromDate)) {
                jobObj.setTraineeFromDate(DateUtil.convertStringToDate(traineeFromDate, DateUtil.IST_DATE_FORMATE));
                DateTimeFormatter df = DateTimeFormatter.ofPattern("d/MM/yyyy");
                LocalDate date = LocalDate.parse(traineeFromDate, df);
                LocalDate traineeToDate = date.plusMonths(jobObj.getTraineeDuration());
                String tDate = traineeToDate.format(DateTimeFormatter.ofPattern("d/MM/yyyy"));
                jobObj.setProbationToDate(DateUtil.convertStringToDate(tDate, DateUtil.IST_DATE_FORMATE));
            }
            String probationFromDate = request.getParameter("probationFromDateStr");
            if (StringUtil.isNotEmpty(probationFromDate)) {
                jobObj.setProbationFromDate(DateUtil.convertStringToDate(probationFromDate, DateUtil.IST_DATE_FORMATE));
                DateTimeFormatter df = DateTimeFormatter.ofPattern("d/MM/yyyy");
                LocalDate date = LocalDate.parse(probationFromDate, df);
                LocalDate probationToDate = date.plusMonths(jobObj.getProbationDuration());
                String pbDate = probationToDate.format(DateTimeFormatter.ofPattern("d/MM/yyyy"));
                jobObj.setProbationToDate(DateUtil.convertStringToDate(pbDate, DateUtil.IST_DATE_FORMATE));

            }

            JobInfoValidator validatior = new JobInfoValidator();
            validatior.validate(jobObj, result);

            if (result.hasErrors()) {
                model.addAttribute("jobObj", jobObj);

                res.setStatus("FAIL");

                Map<String, String> errors = new HashMap<String, String>();
                errors = result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                res.setResult(errors);
                return res;
            }

            if (companyId != null) {
                Optional<CompanyMaster> cm = companyMasterRepository.findById(companyId);
                if (cm.isPresent()) {
                    jobObj.setCompany(cm.get());
                }
            }
            if (companyBranchId != null) {
                Optional<CompanyBranchMaster> cbm = companyBranchMasterRepository.findById(companyBranchId);
                if (cbm.isPresent()) {
                    jobObj.setCompanyBranch(cbm.get());
                }
            }

            List<Object> rm = jobRepo.findWorklocation(companyId, companyBranchId);
            System.out.println(rm);

            model.addAttribute("rm", rm);

            String Worklocation = request.getParameter("worklocationTextarea");
            if (StringUtil.isNotEmpty(Worklocation)) {
                jobObj.setWorklocation(Worklocation);
            }

            if (jobObj.getDesignation() == null || jobObj.getDesignation().getId() == null) {
                jobObj.setDesignation(null);
            }
            if (jobObj.getGroupId() == null || jobObj.getGroupId().getId() == null) {
                jobObj.setGradePayId(null);
            }
            if (jobObj.getPayCommissionId() == null || jobObj.getPayCommissionId().getId() == null) {
                jobObj.setPayCommissionId(null);
            }
            if (jobObj.getPayBand() == null || jobObj.getPayBand().getId() == null) {
                jobObj.setPayBand(null);
            }
            if (jobObj.getGradePayId() == null || jobObj.getGradePayId().getId() == null) {
                jobObj.setGradePayId(null);
            }
            if (jobObj.getRecruitmentTypeId() == null || jobObj.getRecruitmentTypeId().getId() == null) {
                jobObj.setRecruitmentTypeId(null);
            }
            if (jobObj.getShiftpolicyMaster() == null || jobObj.getShiftpolicyMaster().getId() == null) {
                jobObj.setShiftpolicyMaster(null);
            }

            if (jobObj.getWeeklyOff() == null || jobObj.getWeeklyOff().getId() == null) {
                jobObj.setWeeklyOff(null);
            }

            System.err.println("---" + jobObj);
            if (jobObj.getId() == null) {
                jobObj.setRecruitementType(request.getParameter("recruitmrnttype"));
                Job job = jobService.save(jobObj);
                if (job != null) {
                    Job jobobj = (Job) job;
                    res.setObj(jobobj);
                    res.setStatus("SUCCESS");
                }
                auditTrailService.saveAuditTrailData("Employee Job Details", "Save", "Admin",
                        NotificationModule.EMPLOYEE_JOB_DETAILS, NotificationAction.ADD, "/save", userId);
            } else {
                System.err.println("in else");
                jobObj.setRecruitementType(request.getParameter("recruitmrnttype"));
                Job job = jobService.save(jobObj);
                if (job != null) {
                    Job jobobj = (Job) job;
                    res.setObj(jobobj);
                    res.setStatus("UPDATE");
                }
                auditTrailService.saveAuditTrailData("Employee Job Details", "Update", "Admin",
                        NotificationModule.EMPLOYEE_JOB_DETAILS, NotificationAction.UPDATE, "/save", userId);
            }
            System.err.println("res2 " + res);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update employee job Details, " + e.getMessage());
        }
        return res;
    }

    @PostMapping(value = "/saveAddressInfo")
    public @ResponseBody JsonResponse saveAddressInfo(HttpServletRequest request, HttpSession session, Model model,
                                                      @ModelAttribute("employeeAddressObj") EmpAddressDtl employeeAddressObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

//			if(employeeAddressObj.getAllAddSame() != "") {
//				if(employeeAddressObj.getAllAddSame() == "Y") {
//					employeeAddressObj.setAllAddSame("Y");
//				}else {
//					employeeAddressObj.setAllAddSame("N");
//				}
//			}else {
//				employeeAddressObj.setAllAddSame("N");
//			}

            System.out.println("um.getRoleMasterId().getIsAdmin() : " + um.getRoleMasterId().getIsAdmin());

            if (um.getRoleMasterId().getIsAdmin()) {

                if (employeeAddressObj.getId() == null) {

                    if (employeeAddressObj.getId() == null) {


                        EmpAddressDtl em = empAddressRepository.save(employeeAddressObj);
                        if (em != null) {
                            EmpAddressDtl empAddrObj = (EmpAddressDtl) em;
                            res.setObj(empAddrObj);
                            res.setStatus("SUCCESS");
                        }

                    }
                } else {

                    EmpAddressDtl em = empAddressRepository.save(employeeAddressObj);
                    if (em != null) {
                        EmpAddressDtl empAddrObj = (EmpAddressDtl) em;
                        res.setObj(empAddrObj);
                        res.setStatus("UPDATE");
                    }
                }

                auditTrailService.saveAuditTrailData("Employee Address info", "Save", "Admin",
                        NotificationModule.EMPLOYEE_ADDRESS_INFO, NotificationAction.ADD, "/save", userId);
            } else {

                List<EmpAddressDtl> empAddDtlInfo = empAddressRepository.findByEmpId(um.getEmpId().getId());
                System.out.println("=-------------------------------------"+um.getEmpId().getId());
                if (empAddDtlInfo.size() == 0) {
                    res = saveEmpAddressInfoTemp(employeeAddressObj, request, result, model, userId);
                } else {
                    res = compareFieldsEmpAddressInfoTemp(employeeAddressObj, request, result, model, userId, session);
                }
                return res;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update employee address information, " + e.getMessage());
        }
        return res;
    }

    @PostMapping(value = "/saveContactInfo")
    public @ResponseBody JsonResponse saveContactInfo(HttpServletRequest request, HttpSession session, Model model,
                                                      @ModelAttribute("employeeContactObj") EmpContactDtl employeeContactObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

//			EmpPersonalInfoValidator validatior = new EmpPersonalInfoValidator();
//			validatior.validate(employeePersonalObj, result);
//			if (result.hasErrors()) {
//				model.addAttribute("employeePersonalObj", employeePersonalObj);
//
//				res.setStatus("FAIL");
//
//				Map<String, String> errors = new HashMap<String, String>();
//				errors = result.getFieldErrors().stream()
//						.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
//				res.setResult(errors);
//				return res;
//			}

            if (um.getRoleMasterId().getIsAdmin()) {

                if (employeeContactObj.getId() == null) {

                    EmpContactDtl em = empContactRepository.save(employeeContactObj);
                    if (em != null) {
                        EmpContactDtl empContactObj = (EmpContactDtl) em;
                        res.setObj(empContactObj);
                        res.setStatus("SUCCESS");
                    }
                    auditTrailService.saveAuditTrailData("Employee Contact info", "Save", "Admin",
                            NotificationModule.EMPLOYEE_CONTACT_INFO, NotificationAction.ADD, "/save", userId);
                } else {

                    EmpContactDtl em = empContactRepository.save(employeeContactObj);
                    if (em != null) {
                        res.setStatus("UPDATE");
                    }
                    auditTrailService.saveAuditTrailData("Employee Contact info", "Update", "Admin",
                            NotificationModule.EMPLOYEE_CONTACT_INFO, NotificationAction.UPDATE, "/save", userId);
                }
            } else {
                // System.out.println("um.getEmpId().getId() : " + um.getEmpId().getId());
                // EmpContactDtl empContactInfo =
                // empContactRepository.findByEmpId(um.getEmpId().getId());
                EmpContactDtlTemp empContactInfo = empContactDtlTempRepository.findByEmpId(um.getEmpId().getId());
                if (empContactInfo == null) {
                    res = saveEmpContactInfoTemp(employeeContactObj, request, result, model, userId);
                } else {
                    res = compareFieldsEmpContactInfoTemp(employeeContactObj, request, result, model, userId);
                }
                return res;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update employee contact information, " + e.getMessage());
        }
        return res;
    }

    @PostMapping(value = "/saveFamilyInfo")
    public @ResponseBody JsonResponse save(HttpServletRequest request, HttpSession session,
                                           RedirectAttributes redirectAttributes, Model model,
                                           @ModelAttribute("familyMasterObj") EmpFamilyDtl familyMasterObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            if (companyId != null) {
                Optional<CompanyMaster> cm = companyMasterRepository.findById(companyId);
                if (cm.isPresent()) {
                    familyMasterObj.setCompany(cm.get());
                }
            }
            if (companyBranchId != null) {
                Optional<CompanyBranchMaster> cbm = companyBranchMasterRepository.findById(companyBranchId);
                if (cbm.isPresent()) {
                    familyMasterObj.setCompanyBranch(cbm.get());
                }
            }

            String dobStr = request.getParameter("dateOfBirthdayStr");
            String familyRelationId = request.getParameter("familyRelationId");

            EmpPersonalInfo empPersonalInfo = empPersonalInfoRepository.findByEmpId(familyMasterObj.getEmp().getId());
            FamilyRelationMaster familyRelationMaster = familyRelationMasterService
                    .findById(Long.parseLong(familyRelationId));

            if (familyRelationMaster.getFamilyRelationName().equals("Son")
                    || familyRelationMaster.getFamilyRelationName().equals("Daughter")) {
                int ageDiff;
                if(empPersonalInfo == null) {
                    EmpPersonalInfoTemp empPersonalInfoTemp = empPersonalInfoTempRepository.findByEmpIdAndIsApprovedFalse(familyMasterObj.getEmp().getId());

                    if(empPersonalInfoTemp!=null) {
                        ageDiff = DateUtil.ageDifference(empPersonalInfoTemp.getDateOfBirth(),
                                DateUtil.convertStringToDate(dobStr, DateUtil.IST_DATE_FORMATE));
                    }else {
                        ageDiff = 0;
                    }
                }else {
                    ageDiff = DateUtil.ageDifference(empPersonalInfo.getDateOfBirth(),
                            DateUtil.convertStringToDate(dobStr, DateUtil.IST_DATE_FORMATE));
                }

//				ageDiff = DateUtil.ageDifference(empPersonalInfo.getDateOfBirth(),
//						DateUtil.convertStringToDate(dobStr, DateUtil.IST_DATE_FORMATE));

                if (ageDiff < 18) {
                    res.setStatus("NOTVALIADAGE");
                    return res;
                }
            }

            if (StringUtil.isNotEmpty(dobStr)) {
                familyMasterObj.setDateOfBirthday(DateUtil.convertStringToDate(dobStr, DateUtil.IST_DATE_FORMATE));
            }

            if (request.getParameter("isNominee") != null)
                familyMasterObj.setNominee(true);
            else
                familyMasterObj.setNominee(false);

            if (request.getParameter("isDependent") != null)
                familyMasterObj.setDependent(true);
            else
                familyMasterObj.setDependent(false);

            if (request.getParameter("isResidingWith") != null) {
                familyMasterObj.setResidingWith(true);

                List<EmpAddressDtl> empAppList = empAddressRepository.findByEmpId(familyMasterObj.getEmp().getId());

                if (empAppList.size() > 0) {

                    for (EmpAddressDtl empAddressDtl : empAppList) {
                        if (empAddressDtl.getAddType().equalsIgnoreCase("PRM")) {

                            if (StringUtil.isNotEmpty(empAddressDtl.getAddressEmp())) {

                                String add = empAddressDtl.getAddressEmp() + ", "
                                        + empAddressDtl.getCity().getCityName() + ", "
                                        + empAddressDtl.getState().getStateName() + ", "
                                        + empAddressDtl.getCountry().getCountryName() + ", "
                                        + empAddressDtl.getPincode();
                                familyMasterObj.setAddress(add);
                            }

                        }
                    }
                }

            } else {
                familyMasterObj.setResidingWith(false);
            }

            if (request.getParameter("canbeContactedinEmergencyStr") != null)
                familyMasterObj.setCanbeContactedinEmergency(true);
            else
                familyMasterObj.setCanbeContactedinEmergency(false);

            if (request.getParameter("isPhysicallyDisabled") != null)
                familyMasterObj.setPhysicallyDisabled(true);
            else
                familyMasterObj.setPhysicallyDisabled(false);

            EmpFamilyValidator validatior = new EmpFamilyValidator();
            validatior.validate(familyMasterObj, result);
            if (result.hasErrors()) {
                model.addAttribute("familyMasterObj", familyMasterObj);
                res.setStatus("FAIL");

                Map<String, String> errors = new HashMap<String, String>();
                errors = result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                res.setResult(errors);
                return res;
            }
            if (familyMasterObj.getId() == null) {
                familyMasterObj.setCreatedBy(um.getId());

                if (um.getRoleMasterId().getIsAdmin()) {
                    EmpFamilyDtl cbm = empFamilyRepository.save(familyMasterObj);
                    if (cbm != null) {
                        EmpFamilyDtl empFamilyDtl = (EmpFamilyDtl) cbm;
                        res.setObj(empFamilyDtl);
                        res.setStatus("SUCCESS");
                    }
                    if (familyMasterObj.getcanbeContactedinEmergency() == true) {

                        EmpEmergencyDtl emergencyDtl = new EmpEmergencyDtl();

                        emergencyDtl.setEmp(familyMasterObj.getEmp());
                        emergencyDtl.setFirstNameEmg(familyMasterObj.getFirstNameFamily());
                        emergencyDtl.setMiddleNameEmg(familyMasterObj.getMidddleNameFamily());
                        emergencyDtl.setLastNameEmg(familyMasterObj.getLastNameFamily());
                        emergencyDtl.setMobileNoEmg(familyMasterObj.getContactDetail());
                        emergencyDtl.setFamilyRelationEmg(familyMasterObj.getFamilyRelationId());
                        emergencyDtl.setAddressEmg(familyMasterObj.getAddress());
                        emergencyDtl.setPriority("");
                        emergencyDtl.setPhoneNoEmg("");
                        emergencyDtl.setEmpFamily(cbm);

                        empEmergencyRepository.save(emergencyDtl);
                    }
                } else {

                    EmpFamilyDtlTemp empFamilyDtlTemp = new EmpFamilyDtlTemp();
                    empFamilyDtlTemp.setCompany(familyMasterObj.getCompany());
                    empFamilyDtlTemp.setCompanyBranch(familyMasterObj.getCompanyBranch());
                    empFamilyDtlTemp.setAddress(familyMasterObj.getAddress());
                    empFamilyDtlTemp.setCanbeContactedinEmergency(familyMasterObj.getcanbeContactedinEmergency());
                    empFamilyDtlTemp.setContactDetail(familyMasterObj.getContactDetail());
                    empFamilyDtlTemp.setCreatedBy(familyMasterObj.getCreatedBy());
                    empFamilyDtlTemp.setCreatedDate(familyMasterObj.getCreatedDate());
                    empFamilyDtlTemp.setDateOfBirthday(familyMasterObj.getDateOfBirthday());
                    empFamilyDtlTemp.setDependent(familyMasterObj.getIsDependent());
                    empFamilyDtlTemp.setEmp(familyMasterObj.getEmp());
                    empFamilyDtlTemp.setFamilyRelationId(familyMasterObj.getFamilyRelationId());
                    empFamilyDtlTemp.setFirstNameFamily(familyMasterObj.getFirstNameFamily());
                    empFamilyDtlTemp.setGenderId(familyMasterObj.getGenderId());
                    empFamilyDtlTemp.setLastNameFamily(familyMasterObj.getLastNameFamily());
                    empFamilyDtlTemp.setMaritalStatus(familyMasterObj.getMaritalStatus());
                    empFamilyDtlTemp.setMidddleNameFamily(familyMasterObj.getMidddleNameFamily());
                    empFamilyDtlTemp.setNationality(familyMasterObj.getNationality());
                    empFamilyDtlTemp.setNominee(familyMasterObj.getIsNominee());
                    empFamilyDtlTemp.setOccupation(familyMasterObj.getOccupation());
                    empFamilyDtlTemp.setPhysicallyDisabled(familyMasterObj.getIsPhysicallyDisabled());
                    empFamilyDtlTemp.setResidingWith(familyMasterObj.getIsResidingWith());
                    empFamilyDtlTemp.setIpAddress(familyMasterObj.getIpAddress());

                    EmpFamilyDtlTemp cbm = empFamilyDtlTempRepository.save(empFamilyDtlTemp);
                    if (cbm != null) {
                        EmpFamilyDtlTemp empFamilyDtl = (EmpFamilyDtlTemp) cbm;
                        res.setObj(empFamilyDtl);
                        res.setStatus("SUCCESS");
                    }

                    if (empFamilyDtlTemp.getcanbeContactedinEmergency() == true) {

                        EmpEmergencyDtlTemp emergencyDtlTemp = new EmpEmergencyDtlTemp();

                        emergencyDtlTemp.setEmp(familyMasterObj.getEmp());
                        emergencyDtlTemp.setFirstNameEmg(familyMasterObj.getFirstNameFamily());
                        emergencyDtlTemp.setMiddleNameEmg(familyMasterObj.getMidddleNameFamily());
                        emergencyDtlTemp.setLastNameEmg(familyMasterObj.getLastNameFamily());
                        emergencyDtlTemp.setMobileNoEmg(familyMasterObj.getContactDetail());
                        emergencyDtlTemp.setFamilyRelationEmg(familyMasterObj.getFamilyRelationId());
                        emergencyDtlTemp.setAddressEmg(familyMasterObj.getAddress());
                        emergencyDtlTemp.setPriority("");
                        emergencyDtlTemp.setPhoneNoEmg("");
                        // emergencyDtlTemp.setEmpFamilyId(cbm.getId());


                        emergencyDtlTemp.setTempFamilyId(cbm.getId());



                        empEmergencyDtlTempRepository.save(emergencyDtlTemp);

                        System.out.println("TEMP FAMILY ID-------------------------->"  +emergencyDtlTemp.getTempFamilyId());
                    }
                }

                auditTrailService.saveAuditTrailData("Employee", "Save", "Admin",
                        NotificationModule.EMPLOYEE_FAMILY_DETAILS, NotificationAction.ADD, "/saveFamilyInfo", userId);

            } else {

                familyMasterObj.setCreatedBy(um.getId());
                familyMasterObj.setUpdatedBy(um.getId());
                if (um.getRoleMasterId().getIsAdmin()) {
                    EmpFamilyDtl cbm = empFamilyRepository.save(familyMasterObj);
                    if (cbm != null) {
                        EmpFamilyDtl empFamilyDtl = (EmpFamilyDtl) cbm;
                        res.setObj(empFamilyDtl);
                        res.setStatus("UPDATE");
                    }

                    if (familyMasterObj.getcanbeContactedinEmergency() == false) {
                        EmpFamilyDtl empFamilyDtl = empFamilyRepository
                                .findById(Long.parseLong(request.getParameter("id"))).orElse(null);
                        EmpEmergencyDtl empEmergencyDtl = empEmergencyRepository.findByEmpFamily(empFamilyDtl);
                        if (empEmergencyDtl != null) {
                            empEmergencyRepository.deleteById(empEmergencyDtl.getId());
                        }

                    } else {
                        if (familyMasterObj.getcanbeContactedinEmergency() == true) {

                            EmpFamilyDtl empFamilyDtl = empFamilyRepository
                                    .findById(Long.parseLong(request.getParameter("id"))).orElse(null);

                            EmpEmergencyDtl empEmergencyDtl = empEmergencyRepository.findByEmpFamily(empFamilyDtl);

                            if (empEmergencyDtl != null) {

                            } else {
                                EmpEmergencyDtl emergencyDtl = new EmpEmergencyDtl();

                                emergencyDtl.setEmp(familyMasterObj.getEmp());
                                emergencyDtl.setFirstNameEmg(familyMasterObj.getFirstNameFamily());
                                emergencyDtl.setMiddleNameEmg(familyMasterObj.getMidddleNameFamily());
                                emergencyDtl.setLastNameEmg(familyMasterObj.getLastNameFamily());
                                emergencyDtl.setMobileNoEmg(familyMasterObj.getContactDetail());
                                emergencyDtl.setFamilyRelationEmg(familyMasterObj.getFamilyRelationId());
                                emergencyDtl.setAddressEmg(familyMasterObj.getAddress());
                                emergencyDtl.setPriority("");
                                emergencyDtl.setPhoneNoEmg("");
                                emergencyDtl.setEmpFamily(cbm);

                                empEmergencyRepository.save(emergencyDtl);
                            }
                        } else {

                            EmpFamilyDtl empFamilyDtl = empFamilyRepository
                                    .findById(Long.parseLong(request.getParameter("id"))).orElse(null);

                            EmpEmergencyDtl empEmergencyDtl = empEmergencyRepository.findByEmpFamily(empFamilyDtl);

                            if (empEmergencyDtl != null) {
                                empEmergencyService.deleteByEmpFamily(empFamilyDtl);
                            }
                        }
                    }
                } else {
                    System.err.println("isTempFamilyDetails " + request.getParameter("isTempFamilyDetails"));
                    if (!request.getParameter("isTempFamilyDetails").trim().isEmpty()) {
                        Long id = Long.parseLong(request.getParameter("id"));

                        EmpFamilyDtlTemp empFamilyDtlTemp = empFamilyDtlTempRepository.findById(id).orElse(null);

                        // EmpFamilyDtlTemp empFamilyDtlTemp = new EmpFamilyDtlTemp();
                        empFamilyDtlTemp.setCompany(familyMasterObj.getCompany());
                        empFamilyDtlTemp.setCompanyBranch(familyMasterObj.getCompanyBranch());
                        empFamilyDtlTemp.setAddress(familyMasterObj.getAddress());
                        empFamilyDtlTemp.setCanbeContactedinEmergency(familyMasterObj.getcanbeContactedinEmergency());
                        empFamilyDtlTemp.setContactDetail(familyMasterObj.getContactDetail());
                        empFamilyDtlTemp.setCreatedBy(familyMasterObj.getCreatedBy());
                        empFamilyDtlTemp.setCreatedDate(familyMasterObj.getCreatedDate());
                        empFamilyDtlTemp.setDateOfBirthday(familyMasterObj.getDateOfBirthday());
                        empFamilyDtlTemp.setDependent(familyMasterObj.getIsDependent());
                        empFamilyDtlTemp.setEmp(familyMasterObj.getEmp());
                        empFamilyDtlTemp.setFamilyRelationId(familyMasterObj.getFamilyRelationId());
                        empFamilyDtlTemp.setFirstNameFamily(familyMasterObj.getFirstNameFamily());
                        empFamilyDtlTemp.setGenderId(familyMasterObj.getGenderId());
                        empFamilyDtlTemp.setLastNameFamily(familyMasterObj.getLastNameFamily());
                        empFamilyDtlTemp.setMaritalStatus(familyMasterObj.getMaritalStatus());
                        empFamilyDtlTemp.setMidddleNameFamily(familyMasterObj.getMidddleNameFamily());
                        empFamilyDtlTemp.setNationality(familyMasterObj.getNationality());
                        empFamilyDtlTemp.setNominee(familyMasterObj.getIsNominee());
                        empFamilyDtlTemp.setOccupation(familyMasterObj.getOccupation());
                        empFamilyDtlTemp.setPhysicallyDisabled(familyMasterObj.getIsPhysicallyDisabled());
                        empFamilyDtlTemp.setResidingWith(familyMasterObj.getIsResidingWith());
                        empFamilyDtlTemp.setIpAddress(request.getRemoteAddr());

                        EmpFamilyDtlTemp cbm = empFamilyDtlTempRepository.save(empFamilyDtlTemp);
                        if (cbm != null) {
                            EmpFamilyDtlTemp empFamilyDtl = (EmpFamilyDtlTemp) cbm;
                            res.setObj(empFamilyDtl);
                            res.setStatus("UPDATE");
                        }

                        if (empFamilyDtlTemp.getcanbeContactedinEmergency() == false) {
                            if (empEmergencyDtlTempRepository
                                    .findByTempFamilyIdAndIsApprovedFalse(empFamilyDtlTemp.getId()).isPresent()) {
                                EmpEmergencyDtlTemp empEmergencyDtlTemp = empEmergencyDtlTempRepository
                                        .findByTempFamilyIdAndIsApprovedFalse(empFamilyDtlTemp.getId()).orElse(null);
                                if (empEmergencyDtlTemp != null) {
                                    empEmergencyDtlTempRepository.deleteById(empEmergencyDtlTemp.getId());
                                }
                            }

                        } else if (empFamilyDtlTemp.getcanbeContactedinEmergency() == true) {
                            if (!empEmergencyDtlTempRepository
                                    .findByTempFamilyIdAndIsApprovedFalse(empFamilyDtlTemp.getId()).isPresent()) {
                                EmpEmergencyDtlTemp emergencyDtlTemp = new EmpEmergencyDtlTemp();

                                emergencyDtlTemp.setEmp(familyMasterObj.getEmp());
                                emergencyDtlTemp.setFirstNameEmg(familyMasterObj.getFirstNameFamily());
                                emergencyDtlTemp.setMiddleNameEmg(familyMasterObj.getMidddleNameFamily());
                                emergencyDtlTemp.setLastNameEmg(familyMasterObj.getLastNameFamily());
                                emergencyDtlTemp.setMobileNoEmg(familyMasterObj.getContactDetail());
                                emergencyDtlTemp.setFamilyRelationEmg(familyMasterObj.getFamilyRelationId());
                                emergencyDtlTemp.setAddressEmg(familyMasterObj.getAddress());
                                emergencyDtlTemp.setPriority("");
                                emergencyDtlTemp.setPhoneNoEmg("");
                                // emergencyDtlTemp.setEmpFamilyId(cbm.getId());

                                emergencyDtlTemp.setTempFamilyId(cbm.getId());

                                empEmergencyDtlTempRepository.save(emergencyDtlTemp);
                            }
                        }

                    } else {
                        EmpFamilyDtl empFamily = empFamilyRepository.findByIdAndIsDeleteFalse(familyMasterObj.getId());

                        EmpFamilyDtlTemp empFamilyDtlTemp = new EmpFamilyDtlTemp();

                        if (empFamilyDtlTempRepository.findByEmpFamilyDtlMstIdAndIsApprovedFalseAnd(empFamily.getId())
                                .isPresent()) {

                            empFamilyDtlTemp = empFamilyDtlTempRepository
                                    .findByEmpFamilyDtlMstIdAndIsApprovedFalseAnd(empFamily.getId()).get();
                        }

                        empFamilyDtlTemp.setIsEditedRecord(true);
                        empFamilyDtlTemp.setIsDeletedRecord(false);
                        empFamilyDtlTemp.setEmpFamilyDtlMstId(empFamily.getId());
                        empFamilyDtlTemp.setEmp(empFamily.getEmp());
                        empFamilyDtlTemp.setCompany(empFamily.getCompany());
                        empFamilyDtlTemp.setCompanyBranch(empFamily.getCompanyBranch());
                        empFamilyDtlTemp.setIpAddress(request.getRemoteAddr());

                        if (!Objects.equal(empFamily.getAddress(), familyMasterObj.getAddress())) {
                            empFamilyDtlTemp.setAddress(familyMasterObj.getAddress());
                        }

                        if (!Objects.equal(empFamily.getContactDetail(), familyMasterObj.getContactDetail()))
                            empFamilyDtlTemp.setContactDetail(familyMasterObj.getContactDetail());

                        if (!Objects.equal(familyMasterObj.getDateOfBirthday(), empFamily.getDateOfBirthday()))
                            empFamilyDtlTemp.setDateOfBirthday(familyMasterObj.getDateOfBirthday());

                        if ((!familyMasterObj.getIsDependent() && empFamily.getIsDependent())
                                || (familyMasterObj.getIsDependent() && !empFamily.getIsDependent())) {
                            empFamilyDtlTemp.setDependent(familyMasterObj.getIsDependent());
                        } else {
                            if (familyMasterObj.getIsDependent() && empFamily.getIsDependent()) {
                                empFamilyDtlTemp.setDependent(true);
                            } else {
                                empFamilyDtlTemp.setDependent(false);
                            }
                        }

                        if (!Objects.equal(empFamily.getFamilyRelationId(), familyMasterObj.getFamilyRelationId())) {
                            empFamilyDtlTemp.setFamilyRelationId(familyMasterObj.getFamilyRelationId());
                        }

                        if (!Objects.equal(empFamily.getFirstNameFamily(), familyMasterObj.getFirstNameFamily())) {
                            empFamilyDtlTemp.setFirstNameFamily(familyMasterObj.getFirstNameFamily());
                        }

                        if (!Objects.equal(empFamily.getGenderId(), familyMasterObj.getGenderId())) {
                            empFamilyDtlTemp.setGenderId(familyMasterObj.getGenderId());
                        }

                        if (!Objects.equal(empFamily.getLastNameFamily(), familyMasterObj.getLastNameFamily())) {
                            empFamilyDtlTemp.setLastNameFamily(familyMasterObj.getLastNameFamily());
                        }

                        if (!Objects.equal(empFamily.getMaritalStatus(), familyMasterObj.getMaritalStatus())) {
                            empFamilyDtlTemp.setMaritalStatus(familyMasterObj.getMaritalStatus());
                        }

                        if (!Objects.equal(empFamily.getMidddleNameFamily(), familyMasterObj.getMidddleNameFamily())) {
                            empFamilyDtlTemp.setMidddleNameFamily(familyMasterObj.getMidddleNameFamily());
                        }

                        if (!Objects.equal(empFamily.getNationality(), familyMasterObj.getNationality())) {
                            empFamilyDtlTemp.setNationality(familyMasterObj.getNationality());
                        }

                        if ((!empFamily.getIsNominee() && familyMasterObj.getIsNominee())
                                || (empFamily.getIsNominee() && !familyMasterObj.getIsNominee())) {
                            empFamilyDtlTemp.setNominee(familyMasterObj.getIsNominee());
                        } else {
                            if (empFamily.getIsNominee() && familyMasterObj.getIsNominee()) {
                                empFamilyDtlTemp.setNominee(true);
                            } else {
                                empFamilyDtlTemp.setNominee(false);
                            }
                        }

                        if (!Objects.equal(empFamily.getOccupation(), familyMasterObj.getOccupation())) {
                            empFamilyDtlTemp.setOccupation(familyMasterObj.getOccupation());
                        }

                        if ((!empFamily.getIsPhysicallyDisabled() && familyMasterObj.getIsPhysicallyDisabled())
                                || (empFamily.getIsPhysicallyDisabled()
                                && !familyMasterObj.getIsPhysicallyDisabled())) {
                            empFamilyDtlTemp.setPhysicallyDisabled(familyMasterObj.getIsPhysicallyDisabled());
                        } else {
                            if (empFamily.getIsPhysicallyDisabled() && familyMasterObj.getIsPhysicallyDisabled()) {
                                empFamilyDtlTemp.setPhysicallyDisabled(true);
                            } else {
                                empFamilyDtlTemp.setPhysicallyDisabled(false);
                            }
                        }

                        if ((!empFamily.getIsResidingWith() && familyMasterObj.getIsResidingWith())
                                || (empFamily.getIsResidingWith() && !familyMasterObj.getIsResidingWith())) {
                            empFamilyDtlTemp.setResidingWith(familyMasterObj.getIsResidingWith());
                        } else {
                            if (empFamily.getIsResidingWith() && familyMasterObj.getIsResidingWith()) {
                                empFamilyDtlTemp.setResidingWith(true);
                            } else {
                                empFamilyDtlTemp.setResidingWith(false);
                            }
                        }

                        if ((!empFamily.getcanbeContactedinEmergency()
                                && familyMasterObj.getcanbeContactedinEmergency())
                                || (empFamily.getcanbeContactedinEmergency()
                                && !familyMasterObj.getcanbeContactedinEmergency())) {
                            empFamilyDtlTemp
                                    .setCanbeContactedinEmergency(familyMasterObj.getcanbeContactedinEmergency());
                        } else {
                            if (empFamily.getcanbeContactedinEmergency()
                                    && familyMasterObj.getcanbeContactedinEmergency()) {
                                empFamilyDtlTemp.setCanbeContactedinEmergency(true);
                            } else {
                                empFamilyDtlTemp.setCanbeContactedinEmergency(false);
                            }
                        }

                        EmpFamilyDtlTemp cbm = empFamilyDtlTempRepository.save(empFamilyDtlTemp);
                        if (cbm != null) {
                            EmpFamilyDtlTemp empFamilyDtl = (EmpFamilyDtlTemp) cbm;
                            res.setObj(empFamilyDtl);
                            res.setStatus("UPDATE");
                        }

                        if (cbm.getcanbeContactedinEmergency() == false) {

                            EmpEmergencyDtlTemp empEmergencyDtlTemp = new EmpEmergencyDtlTemp();
                            if (empFamily.getcanbeContactedinEmergency()) {
                                EmpEmergencyDtl empEmergencyDtl = empEmergencyRepository.findByEmpFamily(empFamily);

                                if (empEmergencyDtlTempRepository.findByEmpFamilyIdAndIsApprovedFalse(empFamily.getId())
                                        .isPresent()) {
                                    empEmergencyDtlTemp = empEmergencyDtlTempRepository
                                            .findByEmpFamilyIdAndIsApprovedFalse(empFamily.getId()).get();

                                }
                                empEmergencyDtlTemp.setEmpFamilyId(empFamily.getId());
                                empEmergencyDtlTemp.setEmpEmergencyDtlMstId(empEmergencyDtl.getId());
                                empEmergencyDtlTemp.setIsDeletedRecord(true);

                                empEmergencyDtlTempRepository.save(empEmergencyDtlTemp);
                            }
                        } else if (cbm.getcanbeContactedinEmergency() == true) {
                            if(empEmergencyRepository.findByEmpFamily(empFamily) == null ) {
                                if (!empEmergencyDtlTempRepository.findByEmpFamilyIdAndIsApprovedFalse(empFamily.getId())
                                        .isPresent()) {
                                    EmpEmergencyDtlTemp emergencyDtlTemp = new EmpEmergencyDtlTemp();

                                    emergencyDtlTemp.setEmp(familyMasterObj.getEmp());
                                    emergencyDtlTemp.setFirstNameEmg(familyMasterObj.getFirstNameFamily());
                                    emergencyDtlTemp.setMiddleNameEmg(familyMasterObj.getMidddleNameFamily());
                                    emergencyDtlTemp.setLastNameEmg(familyMasterObj.getLastNameFamily());
                                    emergencyDtlTemp.setMobileNoEmg(familyMasterObj.getContactDetail());
                                    emergencyDtlTemp.setFamilyRelationEmg(familyMasterObj.getFamilyRelationId());
                                    emergencyDtlTemp.setAddressEmg(familyMasterObj.getAddress());
                                    emergencyDtlTemp.setPriority("");
                                    emergencyDtlTemp.setPhoneNoEmg("");
                                    emergencyDtlTemp.setEmpFamilyId(empFamily.getId());
                                    emergencyDtlTemp.setTempFamilyId(cbm.getId());

                                    empEmergencyDtlTempRepository.save(emergencyDtlTemp);
                                }
                            }

//							if (!empEmergencyDtlTempRepository.findByEmpFamilyIdAndIsApprovedFalse(empFamily.getId())
//									.isPresent()) {
//								EmpEmergencyDtlTemp emergencyDtlTemp = new EmpEmergencyDtlTemp();
//
//								emergencyDtlTemp.setEmp(familyMasterObj.getEmp());
//								emergencyDtlTemp.setFirstNameEmg(familyMasterObj.getFirstNameFamily());
//								emergencyDtlTemp.setMiddleNameEmg(familyMasterObj.getMidddleNameFamily());
//								emergencyDtlTemp.setLastNameEmg(familyMasterObj.getLastNameFamily());
//								emergencyDtlTemp.setMobileNoEmg(familyMasterObj.getContactDetail());
//								emergencyDtlTemp.setFamilyRelationEmg(familyMasterObj.getFamilyRelationId());
//								emergencyDtlTemp.setAddressEmg(familyMasterObj.getAddress());
//								emergencyDtlTemp.setPriority("");
//								emergencyDtlTemp.setPhoneNoEmg("");
//								emergencyDtlTemp.setEmpFamilyId(empFamily.getId());
//
//								empEmergencyDtlTempRepository.save(emergencyDtlTemp);
//							}
                        }
                    }

                }
                auditTrailService.saveAuditTrailData("Employee", "Update", "Admin",
                        NotificationModule.EMPLOYEE_FAMILY_DETAILS, NotificationAction.UPDATE, "/saveFamilyInfo",
                        userId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update Family master, " + e.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "/getFamilyList/{id}")
    public @ResponseBody JsonResponse getFamilyList(@PathVariable("id") Long id, Model model,
                                                    HttpServletRequest request, HttpSession session) {
        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        JsonResponse res = new JsonResponse();
        List<EmpFamilyDtl> empFamilyList = empFamilyRepository.findByEmpId(id);
        List<Object> empfamList = new ArrayList<Object>();
        empfamList.addAll(empFamilyList);
        if (empfamList.size() > 0) {
            res.setStatus("success");
            res.setObjList(empfamList);
        } else {

            if (!um.getRoleMasterId().getIsAdmin()) {
                List<EmpFamilyDtlTemp> empFamilyDtlTemps = empFamilyDtlTempRepository.findAllByEmpIdAndIsApprovedFalseAndEmpFamilyDtlMstIdIsNull(id);

                if (!empFamilyDtlTemps.isEmpty()) {
                    List<Object> empfamTempList = new ArrayList<Object>();
                    empfamTempList.addAll(empFamilyDtlTemps);
                    res.setStatus("success");
                    res.setTempTableData(true);
                    res.setObjList(empfamTempList);
                }
            }
        }
        return res;
    }

    @RequestMapping(value = "/editEmpFamily")
    public @ResponseBody JsonResponse editEmpFamily(@RequestParam("id") Long id,
                                                    @RequestParam("isTempData") Boolean isTempData, HttpSession session) {
        logger.info("employee address id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        if (um.getRoleMasterId().getIsAdmin()) {
            Optional<EmpFamilyDtl> empFamily = empFamilyRepository.findById(id);

            if (empFamily.isPresent()) {

                EmpFamilyDtl empFamilyObj = (EmpFamilyDtl) empFamily.get();
                res.setObj(empFamilyObj);

                res.setStatus("SUCCESS");
            } else {
                res.setStatus("FAIL");
            }
        } else {
            if (isTempData) {
                EmpFamilyDtlTemp empFamilyDtlTemp = empFamilyDtlTempRepository.findById(id).orElse(null);

                res.setObj(empFamilyDtlTemp);

                res.setStatus("SUCCESS");
            } else {
                Optional<EmpFamilyDtl> empFamily = empFamilyRepository.findById(id);

                if (empFamily.isPresent()) {

                    EmpFamilyDtl empFamilyObj = (EmpFamilyDtl) empFamily.get();
                    res.setObj(empFamilyObj);

                    res.setStatus("SUCCESS");
                } else {
                    res.setStatus("FAIL");
                }
            }
        }

//		Optional<EmpFamilyDtl> empFamily = empFamilyRepository.findById(id);
//		if (empFamily.isPresent()) {
//
//			EmpFamilyDtl empFamilyObj = (EmpFamilyDtl) empFamily.get();
//			res.setObj(empFamilyObj);
//
//			res.setStatus("SUCCESS");
//		}else {
//			EmpFamilyDtlTemp empFamilyDtlTemp = empFamilyDtlTempRepository.findById(id).orElse(null);
//
//			res.setObj(empFamilyDtlTemp);
//
//			res.setStatus("SUCCESS");
//		}
        return res;
    }

    @GetMapping(value = "/deleteFamilyDtl/{id}")
    public @ResponseBody JsonResponse deleteFamilyDtl(@PathVariable("id") Long id, Model model,
                                                      HttpServletRequest request, HttpSession session, @RequestParam("isTempData") Boolean isTempData) {
        logger.info("EmployeeController.deleteFamilyDtl");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            if (um != null) {
                if (um.getRoleMasterId().getIsAdmin()) {
                    Optional<EmpFamilyDtl> familyDtl = empFamilyRepository.findById(id);
                    if (familyDtl.isPresent()) {

                        List<EmpFamilyDtl> empFamilyList = empFamilyRepository
                                .findByEmpId(familyDtl.get().getEmp().getId());
                        List<Object> empROList = new ArrayList<Object>();
                        empROList.addAll(empFamilyList);
                        if (empROList.size() > 0) {
                            res.setObjList(empROList);
                        }

                        EmpFamilyDtl empFamilyDtl = empFamilyRepository.findById(id).orElse(null);
                        EmpEmergencyDtl empEmergencyDtl = empEmergencyRepository.findByEmpFamily(empFamilyDtl);
                        if (empEmergencyDtl != null) {
                            empEmergencyRepository.deleteById(empEmergencyDtl.getId());

                            if (empEmergencyDtlTempRepository
                                    .findByEmpEmergencyDtlMstIdAndIsApprovedFalse((empEmergencyDtl.getId()))
                                    .isPresent()) {
                                EmpEmergencyDtlTemp emergencyDtlTemp = empEmergencyDtlTempRepository
                                        .findByEmpEmergencyDtlMstIdAndIsApprovedFalse((empEmergencyDtl.getId())).get();
                                empEmergencyDtlTempRepository.deleteById(emergencyDtlTemp.getId());
                                res.setStatus("SUCCESS");
                            }
                        }

                        empFamilyService.deleteById(id);

                        if (empFamilyDtlTempRepository.findByEmpFamilyDtlMstIdAndIsApprovedFalseAnd(id).isPresent()) {
                            EmpFamilyDtlTemp empFamilyDtlTemp = empFamilyDtlTempRepository
                                    .findByEmpFamilyDtlMstIdAndIsApprovedFalseAnd(id).get();

                            if (empEmergencyDtlTempRepository.findByEmpFamilyIdAndIsApprovedFalse(id).isPresent()) {
                                EmpEmergencyDtlTemp emergencyDtlTemp = empEmergencyDtlTempRepository
                                        .findByEmpFamilyIdAndIsApprovedFalse(id).get();

                                empEmergencyDtlTempRepository.deleteById(emergencyDtlTemp.getId());
                            }
                            empFamilyDtlTempRepository.deleteById(empFamilyDtlTemp.getId());
                        }

                        res.setStatus("SUCCESS");
                    } else {
                        res.setStatus("FAIL");
                    }
                } else {
                    if (!isTempData) {
                        Optional<EmpFamilyDtl> familyDtl = empFamilyRepository.findById(id);
                        EmpFamilyDtlTemp empFamilyDtlTemp = new EmpFamilyDtlTemp();

                        if (empFamilyDtlTempRepository.findByEmpFamilyDtlMstIdAndIsApprovedFalseAnd(id).isPresent()) {
                            empFamilyDtlTemp = empFamilyDtlTempRepository
                                    .findByEmpFamilyDtlMstIdAndIsApprovedFalseAnd(id).orNull();
                        }

//						if(empFamilyDtlTempRepository.findByEmpFamilyDtlMstIdAndIsApprovedFalseAndDeleteTrue(id).isPresent()) {
//							empFamilyDtlTemp = empFamilyDtlTempRepository.findByEmpFamilyDtlMstIdAndIsApprovedFalseAndDeleteTrue(id).orNull();
//						}
                        if (familyDtl.isPresent()) {
                            empFamilyDtlTemp.setEmpFamilyDtlMstId(id);
                            empFamilyDtlTemp.setIsDeletedRecord(true);
                            empFamilyDtlTemp.setIsEditedRecord(false);
                            empFamilyDtlTemp.setAddress(null);
                            empFamilyDtlTemp.setContactDetail(null);
                            empFamilyDtlTemp.setDateOfBirthday(null);
                            empFamilyDtlTemp.setDependent(false);
                            empFamilyDtlTemp.setEmp(null);
                            empFamilyDtlTemp.setFamilyRelationId(null);
                            empFamilyDtlTemp.setFirstNameFamily(null);
                            empFamilyDtlTemp.setGenderId(null);
                            empFamilyDtlTemp.setLastNameFamily(null);
                            empFamilyDtlTemp.setMaritalStatus(null);
                            empFamilyDtlTemp.setMidddleNameFamily(null);
                            empFamilyDtlTemp.setNationality(null);
                            empFamilyDtlTemp.setNominee(false);
                            empFamilyDtlTemp.setOccupation(null);
                            empFamilyDtlTemp.setPhysicallyDisabled(false);
                            empFamilyDtlTemp.setResidingWith(false);

                            empFamilyDtlTemp = empFamilyDtlTempRepository.save(empFamilyDtlTemp);



                            EmpFamilyDtlHistory empFamilyDtlHistory = new EmpFamilyDtlHistory();

                            empFamilyDtlHistory.setEmpFamilyDtlMstId(empFamilyDtlTemp.getId());
                            empFamilyDtlHistory.setEmp(familyDtl.get().getEmp());
                            empFamilyDtlHistory.setIsDeletedRecord(true);
                            empFamilyDtlHistory.setIsEditedRecord(false);
                            empFamilyDtlHistory.setAddress(familyDtl.get().getAddress());
                            empFamilyDtlHistory.setContactDetail(familyDtl.get().getContactDetail());
                            empFamilyDtlHistory.setDateOfBirthday(familyDtl.get().getDateOfBirthday());
                            empFamilyDtlHistory.setDependent(false);
                            empFamilyDtlHistory.setFamilyRelationId(familyDtl.get().getFamilyRelationId());
                            empFamilyDtlHistory.setFirstNameFamily(familyDtl.get().getFirstNameFamily());
                            empFamilyDtlHistory.setGenderId(familyDtl.get().getGenderId());
                            empFamilyDtlHistory.setLastNameFamily(familyDtl.get().getLastNameFamily());
                            empFamilyDtlHistory.setMaritalStatus(familyDtl.get().getMaritalStatus());
                            empFamilyDtlHistory.setMidddleNameFamily(familyDtl.get().getMidddleNameFamily());
                            empFamilyDtlHistory.setNationality(familyDtl.get().getNationality());
                            empFamilyDtlHistory.setNominee(familyDtl.get().getIsNominee());
                            empFamilyDtlHistory.setOccupation(familyDtl.get().getOccupation());
                            empFamilyDtlHistory.setPhysicallyDisabled(familyDtl.get().getIsPhysicallyDisabled());
                            empFamilyDtlHistory.setResidingWith(familyDtl.get().getIsResidingWith());

                            empFamilyDtlHistoryRepository.save(empFamilyDtlHistory);







//							if(empFamilyDtlTempRepository.findByEmpFamilyDtlMstIdAndIsApprovedFalseAnd(id).isPresent()) {
//								empFamilyDtlTemp = empFamilyDtlTempRepository.findByEmpFamilyDtlMstIdAndIsApprovedFalseAnd(id).get();
//
//								if(empFamilyDtlTemp.getcanbeContactedinEmergency()) {
//									if(empEmergencyDtlTempRepository.findByEmpFamilyIdAndIsApprovedFalse(familyDtl.get().getId()).isPresent()){
//										EmpEmergencyDtlTemp emergencyDtlTemp = empEmergencyDtlTempRepository.findByEmpFamilyIdAndIsApprovedFalse(familyDtl.get().getId()).get();
//
//										empEmergencyDtlTempRepository.deleteById(emergencyDtlTemp.getId());
//									}
////								}
//							}
                            if (familyDtl.get().getcanbeContactedinEmergency()) {

                                EmpEmergencyDtl empEmergencyDtl = empEmergencyRepository.findByEmpFamily(familyDtl.get());

                                EmpEmergencyDtlTemp emergencyDtlTemp = new EmpEmergencyDtlTemp();

//									if(empEmergencyDtlTempRepository.findByEmpEmergencyDtlMstIdAndIsApprovedFalse(empEmergencyDtl.getId()).isPresent()){
//										emergencyDtlTemp = empEmergencyDtlTempRepository.findByEmpEmergencyDtlMstIdAndIsApprovedFalse(empEmergencyDtl.getId()).get();
//									}

                                if (empEmergencyDtlTempRepository
                                        .findByEmpFamilyIdAndIsApprovedFalse(familyDtl.get().getId()).isPresent()) {
                                    emergencyDtlTemp = empEmergencyDtlTempRepository
                                            .findByEmpFamilyIdAndIsApprovedFalse(familyDtl.get().getId()).get();
                                }
                                emergencyDtlTemp.setIsDeletedRecord(true);
                                emergencyDtlTemp.setIsEditedRecord(false);

                                emergencyDtlTemp.setFirstNameEmg(null);
                                emergencyDtlTemp.setMiddleNameEmg(null);
                                emergencyDtlTemp.setLastNameEmg(null);
                                emergencyDtlTemp.setMobileNoEmg(null);
                                emergencyDtlTemp.setFamilyRelationEmg(null);
                                emergencyDtlTemp.setAddressEmg(null);
                                emergencyDtlTemp.setPriority(null);
                                emergencyDtlTemp.setPhoneNoEmg(null);

                                emergencyDtlTemp.setEmpFamilyId(familyDtl.get().getId());
                                emergencyDtlTemp.setEmpEmergencyDtlMstId(empEmergencyDtl.getId());

                                empEmergencyDtlTempRepository.save(emergencyDtlTemp);



                                EmpEmergencyDtlHistory empEmergencyDtlHistory = new EmpEmergencyDtlHistory();

                                empEmergencyDtlHistory.setIsDeletedRecord(true);
                                empEmergencyDtlHistory.setIsEditedRecord(false);

                                empEmergencyDtlHistory.setFirstNameEmg(empEmergencyDtl.getFirstNameEmg());
                                empEmergencyDtlHistory.setMiddleNameEmg(empEmergencyDtl.getMiddleNameEmg());
                                empEmergencyDtlHistory.setLastNameEmg(empEmergencyDtl.getLastNameEmg());
                                empEmergencyDtlHistory.setMobileNoEmg(empEmergencyDtl.getMobileNoEmg());
                                empEmergencyDtlHistory.setFamilyRelationEmg(empEmergencyDtl.getFamilyRelationEmg());
                                empEmergencyDtlHistory.setAddressEmg(empEmergencyDtl.getAddressEmg());
                                empEmergencyDtlHistory.setPriority(empEmergencyDtl.getPriority());
                                empEmergencyDtlHistory.setPhoneNoEmg(empEmergencyDtl.getPhoneNoEmg());

                                empEmergencyDtlHistory.setEmpFamilyId(familyDtl.get().getId());
                                empEmergencyDtlHistory.setEmpEmergencyDtlMstId(emergencyDtlTemp.getId());

                                empEmergencyDtlHistoryRepository.save(empEmergencyDtlHistory);










//									EmpEmergencyDtl empEmergencyDtl = empEmergencyRepository.findByEmpFamily(empFamilyDtlTemp);
//
//									EmpEmergencyDtlTemp emergencyDtlTemp = new EmpEmergencyDtlTemp();
//
//									emergencyDtlTemp.setIsDeletedRecord(true);
//									emergencyDtlTemp.setEmpEmergencyDtlMstId(empEmergencyDtl.getId());
//
//									empEmergencyDtlTempRepository.save(emergencyDtlTemp);

                            }

                            res.setStatus("SUCCESS");
                        } else {
                            res.setStatus("FAIL");
                        }
                    } else {
                        EmpFamilyDtlTemp empFamilyDtlTemp = empFamilyDtlTempRepository.findById(id).orElse(null);
                        if (empFamilyDtlTemp != null) {
//							empFamilyDtlTemp.setIsDeletedRecord(true);
//							empFamilyDtlTemp.setIsEditedRecord(false);
//							empFamilyDtlTemp = empFamilyDtlTempRepository.save(empFamilyDtlTemp);
//							res.setStatus("SUCCESS");
//
                            if (empFamilyDtlTemp.getcanbeContactedinEmergency()) {
                                EmpEmergencyDtlTemp emergencyDtlTemp = empEmergencyDtlTempRepository
                                        .findByTempFamilyIdAndIsApprovedFalse(empFamilyDtlTemp.getId()).orElse(null);

                                if (emergencyDtlTemp != null) {
                                    empEmergencyDtlTempRepository.deleteById(emergencyDtlTemp.getId());
                                }
                            }
                            empFamilyDtlTempRepository.deleteById(id);
                            res.setStatus("SUCCESS");
                        } else {
                            res.setStatus("FAIL");
                        }

                    }

                }

                auditTrailService.saveAuditTrailData("Employee", "Delete", "Admin",
                        NotificationModule.EMPLOYEE_FAMILY_DETAILS, NotificationAction.DELETE, "/deleteFamilyDtl",
                        userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus("FAIL");
        }

        return res;
    }

    @PostMapping(value = "/saveHealthInfo")
    public @ResponseBody JsonResponse saveHealthInfo(HttpServletRequest request, HttpSession session,
                                                     RedirectAttributes redirectAttributes, Model model,
                                                     @ModelAttribute("employeeHealthObj") EmpHealthDtl employeeHealthObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            EmpHealthDtl empHealthDtls = empHealthRepository.findByEmpId(employeeHealthObj.getEmp().getId());


//			if (um.getRoleMasterId().getIsAdmin()) {

//				if(empHealthDtls == null) {

            if (companyId != null) {
                Optional<CompanyMaster> cm = companyMasterRepository.findById(companyId);
                if (cm.isPresent()) {
                    employeeHealthObj.setCompany(cm.get());
                }
            }
            if (companyBranchId != null) {
                Optional<CompanyBranchMaster> cbm = companyBranchMasterRepository.findById(companyBranchId);
                if (cbm.isPresent()) {
                    employeeHealthObj.setCompanyBranch(cbm.get());
                }
            }

            if (request.getParameter("isPhysicallyHandicapped") != null) {
                employeeHealthObj.setPhysicallyHandicapped(true);
                employeeHealthObj.setDisabilityType(employeeHealthObj.getDisabilityType());
            } else {
                employeeHealthObj.setPhysicallyHandicapped(false);
                employeeHealthObj.setDisabilityType(null);
            }

            if (request.getParameter("isFamilyPlanningAllowance") != null)
                employeeHealthObj.setFamilyPlanningAllowance(true);
            else
                employeeHealthObj.setFamilyPlanningAllowance(false);

            EmpHealthValidator validatior = new EmpHealthValidator();
            validatior.validate(employeeHealthObj, result);
            if (result.hasErrors()) {
                model.addAttribute("employeeHealthObj", employeeHealthObj);
                res.setStatus("FAIL");

                Map<String, String> errors = new HashMap<String, String>();
                errors = result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                res.setResult(errors);
                return res;
            }
            if (um.getRoleMasterId().getIsAdmin()) {
//				if (empHealthDtls == null) {
                if (employeeHealthObj.getId() == null) {

                    if (empHealthDtls == null) {
                        employeeHealthObj.setCreatedBy(um.getId());

                        EmpHealthDtl empHeaDtl = empHealthRepository.save(employeeHealthObj);
                        if (empHeaDtl != null) {
                            EmpHealthDtl empHealthDtl = (EmpHealthDtl) empHeaDtl;
                            res.setObj(empHealthDtl);
                            res.setStatus("SUCCESS");
                        }
                        auditTrailService.saveAuditTrailData("Employee", "Save", "Admin",
                                NotificationModule.EMPLOYEE_HEALTH_DETAILS, NotificationAction.ADD, "/saveHealthInfo",
                                userId);
                    }else {
                        res.setStatus("ALREADYEXIST");
                    }

                } else {
                    EmpHealthDtl empHealthDtlsEdit = empHealthRepository.findByEmpIdAndIdNot(employeeHealthObj.getEmp().getId(), employeeHealthObj.getId());

                    if(empHealthDtlsEdit == null) {
                        employeeHealthObj.setCreatedBy(um.getId());
                        employeeHealthObj.setUpdatedBy(um.getId());
                        EmpHealthDtl cbm = empHealthRepository.save(employeeHealthObj);
                        if (cbm != null) {
                            res.setStatus("UPDATE");
                        }
                        auditTrailService.saveAuditTrailData("Employee", "Update", "Admin",
                                NotificationModule.EMPLOYEE_HEALTH_DETAILS, NotificationAction.UPDATE,
                                "/saveHealthInfo", userId);
                    }else {
                        res.setStatus("ALREADYEXIST");
                    }
                }



            }

            else {

                EmpHealthDtl empHeaDtl = empHealthRepository.findByEmpId(um.getEmpId().getId());
                if (empHeaDtl == null) {
                    res = saveEmpHealthDtlTemp(employeeHealthObj, request, result, model, userId, session);
                } else {

                    EmpHealthDtlTemp empHealthDtlTempObj = new EmpHealthDtlTemp();

                    EmpHealthDtl empHealth = empHeaDtl;

                    if (empHealthTempRepository
                            .findByEmpIdAndIsApprovedFalse(employeeHealthObj.getEmp().getId()) != null) {
                        empHealthDtlTempObj = empHealthTempRepository
                                .findByEmpIdAndIsApprovedFalse(employeeHealthObj.getEmp().getId());
                    }

                    // EmpHealthDtlTemp empHealthDtlTemp =
                    // empHealthTempRepository.findByEmpIdAndIsApprovedFalse(employeeHealthDtlObj.getEmp().getId());

                    // if(empHealthDtlTemp == null) {

                    if (companyId != null) {
                        Optional<CompanyMaster> cm = companyMasterRepository.findById(companyId);
                        if (cm.isPresent()) {
                            empHealthDtlTempObj.setCompany(cm.get());
                        }
                    }
                    if (companyBranchId != null) {
                        Optional<CompanyBranchMaster> cbm = companyBranchMasterRepository.findById(companyBranchId);
                        if (cbm.isPresent()) {
                            empHealthDtlTempObj.setCompanyBranch(cbm.get());
                        }
                    }

                    empHealthDtlTempObj.setEmpHealthDtlMstId(empHeaDtl.getId());
                    empHealthDtlTempObj.setEmp(employeeHealthObj.getEmp());
                    empHealthDtlTempObj.setCreatedBy(um.getId());
                    empHealthDtlTempObj.setUpdatedBy(um.getId());
                    empHealthDtlTempObj.setIpAddress(request.getRemoteAddr());

                    if (!Objects.equal(empHealth.getBloodGroup(), employeeHealthObj.getBloodGroup())) {
                        empHealthDtlTempObj.setBloodGroup(employeeHealthObj.getBloodGroup());
                    }

                    if (!Objects.equal(empHealth.getHeight(), employeeHealthObj.getHeight())) {
                        empHealthDtlTempObj.setHeight(employeeHealthObj.getHeight());
                    }

                    if (!Objects.equal(empHealth.getWeight(), employeeHealthObj.getWeight())) {
                        empHealthDtlTempObj.setWeight(employeeHealthObj.getWeight());
                    }

                    if (!Objects.equal(empHealth.getIdentificationMarkFirst(),
                            employeeHealthObj.getIdentificationMarkFirst())) {
                        empHealthDtlTempObj.setIdentificationMarkFirst(employeeHealthObj.getIdentificationMarkFirst());
                    }

                    if (!Objects.equal(empHealth.getIdentificationMarkSecond(),
                            employeeHealthObj.getIdentificationMarkSecond())) {
                        empHealthDtlTempObj
                                .setIdentificationMarkSecond(employeeHealthObj.getIdentificationMarkSecond());
                    }

                    if ((!empHealth.getIsPhysicallyHandicapped() && employeeHealthObj.getIsPhysicallyHandicapped())
                            || (empHealth.getIsPhysicallyHandicapped()
                            && !employeeHealthObj.getIsPhysicallyHandicapped())) {

                        // if (employeeHealthObj.getIsPhysicallyHandicapped()) {
                        empHealthDtlTempObj.setPhysicallyHandicapped(employeeHealthObj.getIsPhysicallyHandicapped());
                        empHealthDtlTempObj.setDisabilityType(employeeHealthObj.getDisabilityType());
//								} else {
//									empHealthDtlTempObj.setPhysicallyHandicapped(false);
//									empHealthDtlTempObj.setDisabilityType(null);
//								}
                    }

                    // }
                    EmpHealthDtlTemp cbm = empHealthTempRepository.save(empHealthDtlTempObj);

                    if (cbm != null) {
                        res.setObj(empHealthDtlTempObj);
                        res.setStatus("SUCCESS");
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update Health master, " + e.getMessage());
        }
        return res;
    }

    @PostMapping(value = "/saveEducatonQualificationInfo")
    public @ResponseBody JsonResponse saveEducatonQualificationInfo(HttpServletRequest request, HttpSession session,
                                                                    RedirectAttributes redirectAttributes, Model model,
                                                                    @ModelAttribute("educationQualificationObj") EmpEducationQualificationDtl educationQualificationObj,
                                                                    BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            if (companyId != null) {
                Optional<CompanyMaster> cm = companyMasterRepository.findById(companyId);
                if (cm.isPresent()) {
                    educationQualificationObj.setCompany(cm.get());
                }
            }
            if (companyBranchId != null) {
                Optional<CompanyBranchMaster> cbm = companyBranchMasterRepository.findById(companyBranchId);
                if (cbm.isPresent()) {
                    educationQualificationObj.setCompanyBranch(cbm.get());
                }
            }

            educationQualificationObj.setCreatedBy(um.getId());
            educationQualificationObj.setCreatedDate(new Date());
            educationQualificationObj.setAppId(1L);
            educationQualificationObj.setIpAddress(request.getRemoteAddr());

            EmpEduQualificationValidator validatior = new EmpEduQualificationValidator();
            validatior.validate(educationQualificationObj, result);
            if (result.hasErrors()) {
                model.addAttribute("educationQualificationObj", educationQualificationObj);
                res.setStatus("FAIL");

                Map<String, String> errors = new HashMap<String, String>();
                errors = result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                res.setResult(errors);
                return res;
            }

            if (um.getRoleMasterId().getIsAdmin()) {

                if (educationQualificationObj.getId() == null) {
                    educationQualificationObj.setCreatedBy(um.getId());

                    EmpEducationQualificationDtl qualificationDtl = educationQualificationDtlRepository
                            .save(educationQualificationObj);
                    if (qualificationDtl != null) {
                        EmpEducationQualificationDtl educationQualificationDtl = (EmpEducationQualificationDtl) qualificationDtl;
                        res.setObj(educationQualificationDtl);
                        res.setStatus("SUCCESS");
                    }
                    auditTrailService.saveAuditTrailData("Employee", "Save", "Admin",
                            NotificationModule.EMPLOYEE_EDUCATION_QUALIFICATION_DETAILS, NotificationAction.ADD,
                            "/saveEducatonQualificationInfo", userId);

                } else {
                    educationQualificationObj.setCreatedBy(um.getId());
                    educationQualificationObj.setUpdatedBy(um.getId());
                    EmpEducationQualificationDtl cbm = educationQualificationDtlRepository
                            .save(educationQualificationObj);
                    if (cbm != null) {
                        EmpEducationQualificationDtl educationQualificationDtl = (EmpEducationQualificationDtl) cbm;
                        res.setObj(educationQualificationDtl);
                        res.setStatus("UPDATE");
                    }
                    auditTrailService.saveAuditTrailData("Employee", "Update", "Admin",
                            NotificationModule.EMPLOYEE_EDUCATION_QUALIFICATION_DETAILS, NotificationAction.UPDATE,
                            "/saveEducatonQualificationInfo", userId);
                }
            } else {
                List<EmpEducationQualificationDtl> empEduDtlInfo = educationQualificationDtlRepository
                        .findByEmpId(um.getEmpId().getId());
                if (empEduDtlInfo.size() == 0) {
                    res = saveEmpEduInfoTemp(educationQualificationObj, request, result, model, userId);
                } else {
                    res = compareFieldsEmpEduQualInfoTemp(educationQualificationObj, request, result, model, userId,
                            session);
                }
                return res;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update Family master, " + e.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "/getEducatonQualificationList/{id}")
    public @ResponseBody JsonResponse getEducatonQualificationList(@PathVariable("id") Long id, Model model,
                                                                   HttpServletRequest request, HttpSession session) {

        JsonResponse res = new JsonResponse();
        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        List<EmpEducationQualificationDtl> empEducationList = educationQualificationDtlRepository.findByEmpId(id);
        List<EmpEducationQualificationDtlTemp> empEducationTempList = empEducationQualificationDtlTempRepository
                .findAllByEmpIdAndIsApprovedFalseAndIsDeletedRecordFalseAndIsEditedRecordFalse(id);
        List<Object> empROList = new ArrayList<Object>();

        if(um.getRoleMasterId().getIsAdmin()) {
            empROList.addAll(empEducationList);
            if (empROList.size() > 0) {
                res.setStatus("success");
                res.setObjList(empROList);
                res.setTempTableData(false);
            }
        }else {
            if (empEducationList.size() == 0) {
                empROList.addAll(empEducationTempList);
                if (empROList.size() > 0) {
                    res.setStatus("success");
                    res.setObjList(empROList);
                    res.setTempTableData(true);
                }
            } else {
                empROList.addAll(empEducationList);
                if (empROList.size() > 0) {
                    res.setStatus("success");
                    res.setObjList(empROList);
                    res.setTempTableData(false);
                }
            }
        }

        return res;
    }

    @RequestMapping(value = "/editEmpEducatonQualification")
    public @ResponseBody JsonResponse editEmpEducatonQualification(@RequestParam("id") Long id, HttpSession session,
                                                                   @RequestParam("tempDataForEducation") boolean tempDataForEducation) {
        logger.info("employee address id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        if (um.getRoleMasterId().getIsAdmin()) {
            Optional<EmpEducationQualificationDtl> empEducation = educationQualificationDtlRepository.findById(id);
            if (empEducation.isPresent()) {

                EmpEducationQualificationDtl empEducationObj = (EmpEducationQualificationDtl) empEducation.get();
                res.setObj(empEducationObj);

                res.setStatus("SUCCESS");
            }
        } else {

            EmpEducationQualificationDtl educationDtl = null;
            EmpEducationQualificationDtlTemp empEduQualTemp = null;
            if (tempDataForEducation) {
                empEduQualTemp = empEducationQualificationDtlTempRepository.findById(id).get();
            } else {
                educationDtl = educationQualificationDtlRepository.findById(id).get();
            }

            if (tempDataForEducation && educationDtl == null) {
                EmpEducationQualificationDtlTemp empEducationObj = (EmpEducationQualificationDtlTemp) empEduQualTemp;
                res.setObj(empEducationObj);
                res.setStatus("SUCCESS");
            } else {
                EmpEducationQualificationDtl empEducationObj = (EmpEducationQualificationDtl) educationDtl;
                res.setObj(empEducationObj);
                res.setStatus("SUCCESS");
            }
        }

        return res;
    }

    @GetMapping(value = "/deleteEducationQualificationDtl")
    public @ResponseBody JsonResponse deleteEducationQualificationDtl(@RequestParam("id") Long id, Model model,
                                                                      HttpServletRequest request, HttpSession session,
                                                                      @RequestParam(value = "tempDataForEducation", defaultValue = "false") Boolean tempDataForEducation) {
        logger.info("EmployeeController.deleteEducationQualificationDtl");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um != null) {
                Optional<EmpEducationQualificationDtl> educationDtl = null;
                if (um.getRoleMasterId().getIsAdmin()) {
                    educationDtl = educationQualificationDtlRepository.findById(id);
                    if (educationDtl.isPresent()) {

                        List<EmpEducationQualificationDtl> empEducationList = educationQualificationDtlRepository
                                .findByEmpId(educationDtl.get().getEmp().getId());
                        List<Object> empROList = new ArrayList<Object>();
                        empROList.addAll(empEducationList);
                        if (empROList.size() > 0) {
                            res.setObjList(empROList);
                        }

                        educationQualificationDtlRepositoryService.deleteById(id);

                        res.setStatus("SUCCESS");
                    } else {
                        res.setStatus("FAIL");
                    }
                } else {

                    EmpEducationQualificationDtlTemp empEduQualTemp = null;
                    EmpEducationQualificationDtlTemp existInTemp = null;
                    if (tempDataForEducation) {
                        empEduQualTemp = empEducationQualificationDtlTempRepository.findById(id).get();
                    } else {
                        existInTemp = empEducationQualificationDtlTempRepository
                                .findByComIdAndBranchIdAndIsApprovedFalseAndMstId(id, companyId, companyBranchId);

                        educationDtl = educationQualificationDtlRepository.findById(id);
                    }

                    if (tempDataForEducation && educationDtl == null) {
                        empEducationQualificationDtlTempRepository.deleteById(id);
                        res.setStatus("SUCCESS");
                    } else {

                        if (educationDtl == null) {
                            empEduQualTemp = new EmpEducationQualificationDtlTemp();
                        } else {
                            if (existInTemp != null) {
                                empEduQualTemp = empEducationQualificationDtlTempRepository
                                        .findByMstId(existInTemp.getId());
                                empEduQualTemp.setUpdatedBy(educationDtl.get().getEmp().getId());
                                empEduQualTemp.setUpdatedDate(new Date());
                            } else if (empEduQualTemp == null) {
                                empEduQualTemp = new EmpEducationQualificationDtlTemp();
                                empEduQualTemp.setCreatedBy(userId);
                                empEduQualTemp.setCreatedDate(new Date());




                            } else {
                                empEduQualTemp = empEducationQualificationDtlTempRepository.findById(id).get();
                                empEduQualTemp.setUpdatedBy(educationDtl.get().getEmp().getId());
                                empEduQualTemp.setUpdatedDate(new Date());
                            }
                        }

                        if (!tempDataForEducation) {
                            empEduQualTemp.setEmpEducationQualificationDtlId(educationDtl.get().getId());
                        } else {
                            empEduQualTemp.setEmpEducationQualificationDtlId(null);

                        }

                        empEduQualTemp.setCompany(companyMasterRepository.findById(companyId).get());
                        empEduQualTemp.setCompanyBranch(companyBranchMasterRepository.findById(companyBranchId).get());

                        empEduQualTemp.setIsDeletedRecord(true);
                        empEduQualTemp.setIsEditedRecord(false);

                        empEduQualTemp.setQualification(null);
                        empEduQualTemp.setModeOfStudy(null);
                        empEduQualTemp.setUniversity(null);
                        empEduQualTemp.setPassingOfMonth(null);
                        empEduQualTemp.setInstitute(null);
                        empEduQualTemp.setPassingYear(null);
                        empEduQualTemp.setDuration(null);
                        empEduQualTemp.setMajor(null);
                        empEduQualTemp.setPercentage(null);
                        empEduQualTemp.setGrade(null);
                        empEduQualTemp.setPercentile(null);
                        empEduQualTemp.setGpaScore(null);
                        empEduQualTemp.setRemark(null);
                        empEduQualTemp.setInstituteAddress(null);
                        empEduQualTemp.setEmp(um.getEmpId());

                        empEducationQualificationDtlTempRepository.save(empEduQualTemp);


//						Here Create History Object for asving Deleted Data


//						EmpEducationQualificationDtlHistory empEducationQualificationDtlHistory = new EmpEducationQualificationDtlHistory();
//
//						empEducationQualificationDtlHistory.setEmp(educationDtl.get().getEmp());
//						empEducationQualificationDtlHistory.setEmpEducationQualificationDtlId(empEduQualTemp.getId());
//						empEducationQualificationDtlHistory.setIsDeletedRecord(true);
//						empEducationQualificationDtlHistory.setIsEditedRecord(false);
//
//						empEducationQualificationDtlHistory.setQualification(educationDtl.get().getQualification());
//						empEducationQualificationDtlHistory.setModeOfStudy(educationDtl.get().getModeOfStudy());
//						empEducationQualificationDtlHistory.setUniversity(educationDtl.get().getUniversity());
//						empEducationQualificationDtlHistory.setPassingOfMonth(educationDtl.get().getPassingOfMonth());
//						empEducationQualificationDtlHistory.setInstitute(educationDtl.get().getInstitute());
//						empEducationQualificationDtlHistory.setPassingYear(educationDtl.get().getPassingYear());
//						empEducationQualificationDtlHistory.setDuration(educationDtl.get().getDuration());
//						empEducationQualificationDtlHistory.setMajor(educationDtl.get().getMajor());
//						empEducationQualificationDtlHistory.setPercentage(educationDtl.get().getPercentage());
//						empEducationQualificationDtlHistory.setGrade(educationDtl.get().getGrade());
//						empEducationQualificationDtlHistory.setPercentile(educationDtl.get().getPercentile());
//						empEducationQualificationDtlHistory.setGpaScore(educationDtl.get().getGpaScore());
//						empEducationQualificationDtlHistory.setRemark(educationDtl.get().getRemark());
//						empEducationQualificationDtlHistory.setInstituteAddress(educationDtl.get().getInstituteAddress());
//
//						empEducationQualificationDtlHistoryRepository.save(empEducationQualificationDtlHistory);










                        res.setStatus("SUCCESS");
                    }
                }

                auditTrailService.saveAuditTrailData("Employee", "Delete", "Admin",
                        NotificationModule.EMPLOYEE_EDUCATION_QUALIFICATION_DETAILS, NotificationAction.DELETE,
                        "/deleteEducationQualificationDtl", userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus("FAIL");
        }

        return res;
    }

    @GetMapping(value = "/deleteAddrDtl")
    public @ResponseBody JsonResponse deleteAddrDtl(@RequestParam("id") Long id, Model model,
                                                    HttpServletRequest request,
                                                    @RequestParam(value = "isTempForAddress", defaultValue = "false") Boolean isTempForAddress,
                                                    HttpSession session) {
        logger.info("EmployeeController.deleteAddrDtl");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            if (um != null) {
                Optional<EmpAddressDtl> emAddDtl = null;
                if (um.getRoleMasterId().getIsAdmin()) {
                    emAddDtl = empAddressRepository.findById(id);
                    if (emAddDtl.isPresent()) {
                        empAddressDtlService.deleteById(id);
                        res.setStatus("SUCCESS");
                    } else {
                        res.setStatus("FAIL");
                    }

                    auditTrailService.saveAuditTrailData("Employee", "Delete", "Admin",
                            NotificationModule.EMPLOYEE_ADDRESS_INFO, NotificationAction.DELETE, "/deleteAddrDtl",
                            userId);
                } else {

                    EmpAddressDtlTemp addressDtlTemp = null;
                    EmpAddressDtlTemp existInTemp = null;

                    EmpAddressDtlHistory empAddressDtlHistory = new EmpAddressDtlHistory();

                    if (isTempForAddress) {
                        addressDtlTemp = empAddressDtlTempRepository.findById(id).get();
                    } else {
//						Changed Here
                        existInTemp = empAddressDtlTempRepository.findByEmpAddressDtlMstIdAndIsApprovedFalse(id);

                        //		existInTemp = empAddressDtlTempRepository.findByEmpAddressDtlMstIdAndIsApprovedTrue(id);
                        emAddDtl = empAddressRepository.findById(id);
                        System.out.println("Address Table------------->"+emAddDtl);
                    }

                    if (isTempForAddress && emAddDtl == null) {
                        empAddressDtlTempRepository.deletByMstId(id);
                        res.setStatus("SUCCESS");
                    } else {

                        if (emAddDtl == null) {
                            addressDtlTemp = new EmpAddressDtlTemp();
                        } else {
                            if (existInTemp != null) {
                                addressDtlTemp = empAddressDtlTempRepository.findById(existInTemp.getId()).get();
                            } else if (addressDtlTemp == null) {
                                addressDtlTemp = new EmpAddressDtlTemp();
                            } else {
                                addressDtlTemp = empAddressDtlTempRepository.findById(id).get();
                            }
                        }

                        if (!isTempForAddress) {
                            addressDtlTemp.setEmpAddressDtlId(emAddDtl.get().getId());





                        } else {
                            addressDtlTemp.setEmpAddressDtlId(null);

                        }



                        addressDtlTemp.setIsDeletedRecord(true);
                        addressDtlTemp.setIsEditedRecord(false);
                        addressDtlTemp.setAddType(null);
                        addressDtlTemp.setAddressEmp(null);
                        addressDtlTemp.setCountry(null);
                        addressDtlTemp.setState(null);
                        addressDtlTemp.setDistrict(null);
                        addressDtlTemp.setCity(null);
                        addressDtlTemp.setPincode(null);
                        addressDtlTemp.setAllAddSame(null);
                        addressDtlTemp.setPropertyType(null);
                        addressDtlTemp.setEmp(um.getEmpId());

                        addressDtlTemp = empAddressDtlTempRepository.save(addressDtlTemp);


                        empAddressDtlHistory.setEmpAddressDtlId(addressDtlTemp.getId());
                        empAddressDtlHistory.setIsDeletedRecord(true);
                        empAddressDtlHistory.setIsEditedRecord(false);

                        empAddressDtlHistory.setAddType(emAddDtl.get().getAddType());
                        empAddressDtlHistory.setAddressEmp(emAddDtl.get().getAddressEmp());
                        empAddressDtlHistory.setCountry(emAddDtl.get().getCountry());
                        empAddressDtlHistory.setState(emAddDtl.get().getState());
                        empAddressDtlHistory.setDistrict(emAddDtl.get().getDistrict());
                        empAddressDtlHistory.setCity(emAddDtl.get().getCity());
                        empAddressDtlHistory.setPincode(emAddDtl.get().getPincode());
                        empAddressDtlHistory.setAllAddSame(emAddDtl.get().getAllAddSame());
                        empAddressDtlHistory.setPropertyType(emAddDtl.get().getPropertyType());
                        empAddressDtlHistory.setEmp(um.getEmpId());







                        empAddressDtlHistoryRepository.save(empAddressDtlHistory);


                        res.setStatus("SUCCESS");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus("FAIL");
        }

        return res;
    }

    @ResponseBody
    @GetMapping(value = "/getpay/{id}")
    public PayBandMaster getpay(@PathVariable("id") Long id, Model model, HttpServletRequest request,
                                final RedirectAttributes redirectAttributes, HttpSession session) {
        logger.info("PayBandMasterController.getpay()" + id);
        PayBandMaster payBandMaster = new PayBandMaster();
        try {

            payBandMaster = payBandService.findById(id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payBandMaster;
    }

    @RequestMapping(value = "/getEmpAddrList/{id}")
    public @ResponseBody JsonResponse getEmpAddrList(@PathVariable("id") Long id, Model model,
                                                     HttpServletRequest request, HttpSession session) {

        JsonResponse res = new JsonResponse();
        UserMaster um = (UserMaster) session.getAttribute("usermaster");

        List<EmpAddressDtl> empAppList = empAddressRepository.findByEmpId(id);

        List<Object> empApprList = new ArrayList<Object>();

        if (um.getRoleMasterId().getIsAdmin()) {
            if (empAppList.size() > 0) {

                for (EmpAddressDtl empAddressDtl : empAppList) {
                    HrmsCode hrmsCodeAddr = hrmsCodeService.findByFieldNameAndCode("ADDRESS_TYPE",
                            empAddressDtl.getAddType());
                    if (hrmsCodeAddr != null) {
                        empAddressDtl.setAddTypeStr(hrmsCodeAddr.getDescription());
                    }

                }
            }

            empApprList.addAll(empAppList);
            if (empApprList.size() > 0) {
                res.setStatus("success");
                res.setObjList(empApprList);
            }
        } else {

            if (empAppList.size() > 0) {

                for (EmpAddressDtl empAddressDtl : empAppList) {
                    HrmsCode hrmsCodeAddr = hrmsCodeService.findByFieldNameAndCode("ADDRESS_TYPE",
                            empAddressDtl.getAddType());
                    if (hrmsCodeAddr != null) {
                        empAddressDtl.setAddTypeStr(hrmsCodeAddr.getDescription());
                    }
                }

                empApprList.addAll(empAppList);
                if (empApprList.size() > 0) {
                    res.setStatus("success");
                    res.setObjList(empApprList);
                    res.setTempTableData(false);
                }

            } else {
                List<EmpAddressDtlTemp> addressDtlTemps = empAddressDtlTempRepository
                        .findAllByEmpIdAndIsApprovedFalse(id);

                if (addressDtlTemps.size() > 0) {
                    for (EmpAddressDtlTemp empAddressDtl : addressDtlTemps) {
                        HrmsCode hrmsCodeAddr = hrmsCodeService.findByFieldNameAndCode("ADDRESS_TYPE",
                                empAddressDtl.getAddType());
                        if (hrmsCodeAddr != null) {
                            empAddressDtl.setAddTypeStr(hrmsCodeAddr.getDescription());
                        }
                    }

                    empApprList.addAll(addressDtlTemps);
                    res.setStatus("success");
                    res.setObjList(empApprList);
                    res.setTempTableData(true);
                }else {
                    res.setStatus("fail");
                }
            }
        }
        return res;
    }

    //added for temp address
    @RequestMapping(value = "/getTempAddrist/{id}")
    public @ResponseBody JsonResponse getTempAddrist(@PathVariable("id") Long id, Model model,
                                                     HttpServletRequest request, HttpSession session) {

        JsonResponse res = new JsonResponse();
        UserMaster um = (UserMaster) session.getAttribute("usermaster");

        List<Object> empApprList = new ArrayList<Object>();

        List<EmpAddressDtlTemp> addressDtlTemps = empAddressDtlTempRepository.findAllByEmpIdAndIsApprovedFalseAndIsDeletedRecordFalse(id);
        if (addressDtlTemps.size() > 0) {
            for (EmpAddressDtlTemp empAddressDtl : addressDtlTemps) {
                HrmsCode hrmsCodeAddr = hrmsCodeService.findByFieldNameAndCode("ADDRESS_TYPE",
                        empAddressDtl.getAddType());
                if (hrmsCodeAddr != null) {
                    empAddressDtl.setAddTypeStr(hrmsCodeAddr.getDescription());
                }
            }

            empApprList.addAll(addressDtlTemps);
            res.setStatus("success");
            res.setObjList(empApprList);
            res.setTempTableData(true);
        }
        return res;
    }


    @RequestMapping(value = "/getAddrTypeList")
    public @ResponseBody JsonResponse getAddrTypeList(@RequestParam("id") Long id, HttpSession session) {
        logger.info("employee id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        List<EmpAddressDtl> empAppList = empAddressRepository.findByEmpId(id);
        List<HrmsCode> addrTypeList = hrmsCodeService.findByFieldName("ADDRESS_TYPE");
        if (empAppList.size() > 0) {

            for (EmpAddressDtl empAddressDtl : empAppList) {
                addrTypeList.removeIf(x -> x.getCode().equalsIgnoreCase(empAddressDtl.getAddType()));
            }
        }

        List<Object> addrType = new ArrayList<Object>();
        addrType.addAll(addrTypeList);
        if (addrTypeList.size() > 0) {
            res.setStatus("success");
            res.setObjList(addrType);
        }
        return res;
    }

    @RequestMapping(value = "/editEmpAddr")
    public @ResponseBody JsonResponse editEmpAddr(@RequestParam("id") Long id,
                                                  @RequestParam(value = "isTempForAddress", defaultValue = "false") Boolean isTempForAddress,
                                                  HttpSession session) {
        logger.info("employee address id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        List<Object> stringList = new ArrayList<Object>();
        if (um.getRoleMasterId().getIsAdmin()) {
            Optional<EmpAddressDtl> empAddr = empAddressRepository.findById(id);
            if (empAddr.isPresent()) {
                List<HrmsCode> addrTypeList2 = hrmsCodeService.findByFieldName("ADDRESS_TYPE");
                stringList.addAll(addrTypeList2);

                System.err.println("size " + stringList.size());
                res.setObjList(stringList);

                EmpAddressDtl empAddrObj = (EmpAddressDtl) empAddr.get();
                res.setObj(empAddrObj);

                res.setStatus("SUCCESS");
            }
        } else {
            if (isTempForAddress) {
                Optional<EmpAddressDtlTemp> empAddrTemp = empAddressDtlTempRepository.findById(id);
                if (empAddrTemp.isPresent()) {

                    List<HrmsCode> addrTypeList2 = hrmsCodeService.findByFieldName("ADDRESS_TYPE");
                    stringList.addAll(addrTypeList2);

                    System.err.println("size " + stringList.size());
                    res.setObjList(stringList);

                    EmpAddressDtlTemp empAddrObj = (EmpAddressDtlTemp) empAddrTemp.get();
                    res.setObj(empAddrObj);

                    res.setStatus("SUCCESS");
                }
            } else {
                Optional<EmpAddressDtl> empAddr = empAddressRepository.findById(id);
                if (empAddr.isPresent()) {
                    List<HrmsCode> addrTypeList2 = hrmsCodeService.findByFieldName("ADDRESS_TYPE");
                    stringList.addAll(addrTypeList2);

                    System.err.println("size " + stringList.size());
                    res.setObjList(stringList);

                    EmpAddressDtl empAddrObj = (EmpAddressDtl) empAddr.get();
                    res.setObj(empAddrObj);

                    res.setStatus("SUCCESS");
                }
            }
        }

        return res;
    }

    @GetMapping(value = "/getEmployeesAutoComplete")
    public @ResponseBody String getEmployeesAutoComplete(HttpServletRequest request, @RequestParam String name,
                                                         @RequestParam String empListFor, HttpSession session) {
        String json = null;
        try {

            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                return "hrms/login";
            }

            logger.info("empListFor " + empListFor);
            if (StringUtil.isNotEmpty(name)) {
                name = name + "%";
            } else {
                name = "%";
            }
            if (StringUtil.isNotEmpty(name)) {
                List<ListItems> listItems = new ArrayList<>();
                List<Employee> employeeList = employeeRepository
                        .findLikeEmployeeByCompanyIdAndCompnayBranchIdAndIsDeleteFalse(name, empListFor, companyId,
                                companyBranchId);
                employeeList.forEach(e -> {
                    ListItems item = new ListItems(e.getId().toString(),
                            e.getFirstName().trim()
                                    + (e.getMiddleName().trim().isEmpty() ? "" : " " + e.getMiddleName().trim())
                                    + " " + e.getLastName().trim()
                                    + " (" + e.getEmpCode().trim() + ")");
                    listItems.add(item);
                });
                json = new ObjectMapper().writeValueAsString(listItems);
                logger.info(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @PostMapping(value = "/saveReportingOfficer")
    public @ResponseBody JsonResponse saveReportingOfficer(HttpServletRequest request, HttpSession session, Model model,
                                                           @ModelAttribute("reportingOfficerObj") EmpReportingOfficer reportingOfficerObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            String sDateStr = request.getParameter("startDateStr");
            if (StringUtil.isNotEmpty(sDateStr)) {
                reportingOfficerObj.setStartDate(DateUtil.convertStringToDate(sDateStr, DateUtil.IST_DATE_FORMATE));
            }

            String endDateStr = request.getParameter("endDateStr");
            if (StringUtil.isNotEmpty(endDateStr)) {
                reportingOfficerObj.setEndDate(DateUtil.convertStringToDate(endDateStr, DateUtil.IST_DATE_FORMATE));
            }

            String authorizationDateStr = request.getParameter("authorizationDateStr");
            if (StringUtil.isNotEmpty(authorizationDateStr)) {
                reportingOfficerObj.setAuthorizationDate(
                        DateUtil.convertStringToDate(authorizationDateStr, DateUtil.IST_DATE_FORMATE));
            }

            EmpReportingOfficerValidator validatior = new EmpReportingOfficerValidator();
            validatior.validate(reportingOfficerObj, result);
            if (result.hasErrors()) {
                model.addAttribute("reportingOfficerObj", reportingOfficerObj);

                res.setStatus("FAIL");

                Map<String, String> errors = new HashMap<String, String>();
                errors = result.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                res.setResult(errors);
                return res;
            }

            if (reportingOfficerObj.getDdo() == null || reportingOfficerObj.getDdo().getId() == null) {
                reportingOfficerObj.setDdo(null);
            }

//			 if(reportingOfficerObj.getDh() == null && reportingOfficerObj.getDh().getId() == null){
//                 reportingOfficerObj.setDh(null);
//             }
//
//			 if(reportingOfficerObj.getHod() == null && reportingOfficerObj.getHod().getId() == null){
//                 reportingOfficerObj.setHod(null);
//             }
//
//			 if(reportingOfficerObj.getHo() == null && reportingOfficerObj.getHo().getId() == null){
//                 reportingOfficerObj.setHo(null);
//             }

            if (reportingOfficerObj.getId() == null) {

                EmpReportingOfficer em = empReportingOfficerRepository.save(reportingOfficerObj);
                if (em != null) {
                    EmpReportingOfficer empObj = (EmpReportingOfficer) em;
                    res.setObj(empObj);
                    res.setStatus("SUCCESS");
                }
                auditTrailService.saveAuditTrailData("Employee reporting officer", "Save", "Admin",
                        NotificationModule.EMPLOYEE_REPORTING_OFFICER, NotificationAction.ADD, "/save", userId);
            } else {
                 EmpReportingOfficerHistory empReportingOfficerHistory = compareAndStoreHistory(empReportingOfficerRepository.findById(reportingOfficerObj.getId()).get(),
                        reportingOfficerObj);
                 empReportingOfficerHistory.setChangeDate(new Date());
                 empReportingOfficerHistory.setChangedBy(um.getId());
                 this.empReportingOfficerRepositoryHistory.save(empReportingOfficerHistory);
                EmpReportingOfficer em = empReportingOfficerRepository.save(reportingOfficerObj);
                if (em != null) {

                    EmpReportingOfficer empObj = (EmpReportingOfficer) em;
                    res.setObj(empObj);
                    res.setStatus("UPDATE");
                }
                auditTrailService.saveAuditTrailData("Employee reporting officer", "Update", "Admin",
                        NotificationModule.EMPLOYEE_REPORTING_OFFICER, NotificationAction.UPDATE, "/save", userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update employee personal information, " + e.getMessage());
        }
        return res;
    }

    private EmpReportingOfficerHistory compareAndStoreHistory(EmpReportingOfficer oldERO, EmpReportingOfficer newERO) {
        EmpReportingOfficerHistory historyRecords = new EmpReportingOfficerHistory();

        // Compare each field and create history records for changes
        if (oldERO.getStartDate() != null && newERO.getStartDate() != null && !oldERO.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .equals(newERO.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
            historyRecords.setStartDate(oldERO.getStartDate());
        }

        if (oldERO.getEndDate() != null && newERO.getEndDate() != null && !oldERO.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .equals(newERO.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
            historyRecords.setEndDate(oldERO.getEndDate());
        }

        if (oldERO.getDdo() != null && newERO.getDdo() != null &&  !oldERO.getDdo().getId().equals(newERO.getDdo().getId())) {
            historyRecords.setDdo(oldERO.getDdo());
        }

        if (oldERO.getDh() != null && newERO.getDh() != null && !oldERO.getDh().getId().equals(newERO.getDh().getId())) {
            historyRecords.setDh(oldERO.getDh());
        }

        if (oldERO.getHo() != null && newERO.getHo() != null && !oldERO.getHo().getId().equals(newERO.getHo().getId())) {
            historyRecords.setHo(oldERO.getHo());
        }

        if (oldERO.getHod() != null && newERO.getHod() != null && !oldERO.getHod().getId().equals(newERO.getHod().getId())) {
           historyRecords.setHod(oldERO.getHod());
        }

        if (oldERO.getAuthorizationDate() != null && newERO.getAuthorizationDate() != null && !oldERO.getAuthorizationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(newERO.getAuthorizationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
            historyRecords.setAuthorizationDate(oldERO.getAuthorizationDate());
        }

        if (oldERO.getNote() != null && !oldERO.getNote().equals(newERO.getNote())) {
            historyRecords.setNote(oldERO.getNote());
        }

         historyRecords.setEmp(oldERO.getEmp());
        return historyRecords;
    }

    @RequestMapping(value = "/editEmpReportingOfficer")
    public @ResponseBody JsonResponse editEmpReportingOfficer(@RequestParam("id") Long id, HttpSession session) {
        logger.info("employee address id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        Optional<EmpReportingOfficer> empOfficer = empReportingOfficerRepository.findById(id);
        if (empOfficer.isPresent()) {

            EmpReportingOfficer reportingOfficerObj = (EmpReportingOfficer) empOfficer.get();
            res.setObj(reportingOfficerObj);

            res.setStatus("SUCCESS");
        }

        return res;
    }

    @RequestMapping(value = "/getOfficerList/{id}")
    public @ResponseBody JsonResponse getOfficerList(@PathVariable("id") Long id, Model model,
                                                     HttpServletRequest request, HttpSession session) {

        JsonResponse res = new JsonResponse();
        List<EmpReportingOfficer> empOfficerList = empReportingOfficerRepository.findByEmpId(id);

        List<Object> empROList = new ArrayList<Object>();
        empROList.addAll(empOfficerList);
        if (empROList.size() > 0) {
            res.setStatus("success");
            res.setObjList(empROList);
        }
        return res;
    }

    @RequestMapping(value = "/editEmpOfficer")
    public @ResponseBody JsonResponse editEmpOfficer(@RequestParam("id") Long id, HttpSession session) {
        logger.info("employee address id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        Optional<EmpReportingOfficer> empOfficer = empReportingOfficerRepository.findById(id);
        if (empOfficer.isPresent()) {

            EmpReportingOfficer reportingOfficerObj = (EmpReportingOfficer) empOfficer.get();
            res.setObj(reportingOfficerObj);
            res.setStatus("SUCCESS");
        }

        return res;
    }

    @GetMapping(value = "/deleteOfficerDtl/{id}")
    public @ResponseBody JsonResponse deleteOfficerDtl(@PathVariable("id") Long id, Model model,
                                                       HttpServletRequest request, HttpSession session) {
        logger.info("EmployeeController.deleteOfficerDtl");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            if (um != null) {
                Optional<EmpReportingOfficer> officerDtl = empReportingOfficerRepository.findById(id);
                if (officerDtl.isPresent()) {

                    List<EmpReportingOfficer> empOfficerList = empReportingOfficerRepository
                            .findByEmpId(officerDtl.get().getEmp().getId());
                    List<Object> empROList = new ArrayList<Object>();
                    empROList.addAll(empOfficerList);
                    if (empROList.size() > 0) {
                        res.setObjList(empROList);
                    }

                    empReportingOfficerService.deleteById(id);

                    res.setStatus("SUCCESS");
                } else {
                    res.setStatus("FAIL");
                }

                auditTrailService.saveAuditTrailData("Employee", "Delete", "Admin",
                        NotificationModule.EMPLOYEE_REPORTING_OFFICER, NotificationAction.DELETE, "/deleteOfficerDtl",
                        userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus("FAIL");
        }

        return res;
    }

    @RequestMapping("/getTempEmployeeCode")
    public @ResponseBody JsonResponse getTempEmployeeCode(
            @RequestParam(value = "empCodePrefix", defaultValue = "0") String empCodePrefix, HttpSession session) {

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        String newEmployeeCode = employeeService.findMaxEmployeeCodeByPrefix(companyId, empCodePrefix);
        res.setStatus("SUCCESS");
        res.setObj(newEmployeeCode);
        return res;
    }

    @RequestMapping("/empCodeVerify")
    public @ResponseBody JsonResponse empCodeVerify(@RequestParam String tempEmpCode,
                                                    @RequestParam(value = "id", defaultValue = "0") Long id, HttpServletRequest request, HttpSession session) {

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        System.err.println("id " + id);
        System.err.println("tempEmpCode " + tempEmpCode);

        if (id.toString().equalsIgnoreCase("0")) {
            if (employeeRepository.findByEmpCodeIgnoreCaseAndIsDeleteFalseAndCompanyId(tempEmpCode,
                    companyId) == null) {
                System.out.println("id " + id + "  " + tempEmpCode);

                res.setStatus("SUCCESS");
                return res;

            } else {
                System.out.println("Fail");

                res.setStatus("FAIL");
                return res;
            }
        } else {
            if (employeeRepository.findByIdAndEmpCodeIgnoreCaseAndIsDeleteFalseAndCompanyId(id, tempEmpCode,
                    companyId) == null) {
                res.setStatus("SUCCESS");
                return res;

            } else {
                res.setStatus("FAIL");
                return res;

            }

        }
    }

    @PostMapping(value = "/empProfileDtl/save")
    public @ResponseBody JsonResponse saveEmpProfileDtl(HttpServletRequest request, HttpSession session,
                                                        @ModelAttribute("employeeProfileObj") EmpPhotoDtl employeeProfileObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

//			System.err.println("---"+employeeProfileObj.getEmp());
//			System.err.println("---"+employeeProfileObj.getEmp().getId());
//			CompanyMasterValidator validatior = new CompanyMasterValidator();
//			validatior.validate(companyMasterObj, result);
//			if (result.hasErrors()) {
//				model.addAttribute("companyMasterObj", companyMasterObj);
//				res.setStatus("FAIL");
//
//				Map<String, String> errors = new HashMap<String, String>();
//				errors = result.getFieldErrors().stream()
//						.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
//				res.setResult(errors);
//				return res;
//			}

            if (employeeProfileObj.getEmp() != null && employeeProfileObj.getEmp().getId() != null) {
                EmpPhotoDtl empPhotoDtl = empPhotoDtlRepository.findByEmpId(employeeProfileObj.getEmp().getId());

                if (empPhotoDtl != null) {
                    FileMaster fileMaster = null;
                    if (null != employeeProfileObj.getProfileImgFile()
                            && !"".equals(employeeProfileObj.getProfileImgFile().getOriginalFilename())) {

                        String name = employeeProfileObj.getProfileImgFile().getOriginalFilename();

                        fileMaster = commonUtility.saveFileObject(name, employeeProfileObj.getProfileImgFile(),
                                CommonConstant.EMP_FILES, companyId, companyBranchId);
                        if (fileMaster != null) {
                            empPhotoDtl.setProfileImg(fileMaster);

                            File convFile = imageResize.convert(employeeProfileObj.getProfileImgFile());
                            String saveFileName = fileMaster.getFileName();
                            String fileNameWithOutExt = SilverUtil.removeExtension(saveFileName);

//							imageResize.Resizepic2D(fileMaster, convFile, CommonConstant.IMG_SIZE_FOR_LIST, CommonConstant.IMG_SIZE_FOR_LIST, CommonConstant.EMP_FILES, companyId, companyBranchId, fileNameWithOutExt+"_list"+fileMaster.getFileType(),CommonConstant.IMG_DESC_LIST);
//
//							imageResize.Resizepic2D(fileMaster, convFile, CommonConstant.IMG_SIZE_FOR_PROFILE, CommonConstant.IMG_SIZE_FOR_PROFILE, CommonConstant.EMP_FILES, companyId, companyBranchId, fileNameWithOutExt+"_profile"+fileMaster.getFileType(),CommonConstant.IMG_DESC_PROFILE);
//
//							imageResize.Resizepic2D(fileMaster, convFile, CommonConstant.IMG_SIZE_FOR_VIEW, CommonConstant.IMG_SIZE_FOR_VIEW, CommonConstant.EMP_FILES, companyId, companyBranchId, fileNameWithOutExt+"_view"+fileMaster.getFileType(),CommonConstant.IMG_DESC_VIEW);

                            imageResize.resizeImageUsingThumbnails(fileMaster, convFile,
                                    CommonConstant.IMG_SIZE_FOR_LIST, CommonConstant.IMG_SIZE_FOR_LIST,
                                    CommonConstant.EMP_FILES, companyId, companyBranchId,
                                    fileNameWithOutExt + "_list" + fileMaster.getFileType(),
                                    CommonConstant.IMG_DESC_LIST);

                            imageResize.resizeImageUsingThumbnails(fileMaster, convFile,
                                    CommonConstant.IMG_SIZE_FOR_PROFILE, CommonConstant.IMG_SIZE_FOR_PROFILE,
                                    CommonConstant.EMP_FILES, companyId, companyBranchId,
                                    fileNameWithOutExt + "_profile" + fileMaster.getFileType(),
                                    CommonConstant.IMG_DESC_PROFILE);

                            imageResize.resizeImageUsingThumbnails(fileMaster, convFile,
                                    CommonConstant.IMG_SIZE_FOR_VIEW, CommonConstant.IMG_SIZE_FOR_VIEW,
                                    CommonConstant.EMP_FILES, companyId, companyBranchId,
                                    fileNameWithOutExt + "_view" + fileMaster.getFileType(),
                                    CommonConstant.IMG_DESC_VIEW);
                        }

                    } else {
                        empPhotoDtl.setProfileImg(employeeProfileObj.getProfileImg());
                    }

                    if (um.getRoleMasterId().getIsAdmin()) {
                        EmpPhotoDtl ep = empPhotoDtlRepository.save(empPhotoDtl);
                        if (ep != null) {
                            res.setStatus("SUCCESS");
                            res.setObj(ep.getEmp().getId());
                        }
                    } else {
                        EmpPhotoDtlTemp ep = new EmpPhotoDtlTemp();

//						String name = employeeProfileObj.getProfileImgFile().getOriginalFilename();

//						fileMaster = commonUtility.saveFileObject(name,
//								employeeProfileObj.getProfileImgFile(), CommonConstant.EMP_FILES, companyId,
//								companyBranchId);

                        if (empPhotoDtlTempRepository
                                .findByEmpIdAndIsApprovedFalse(employeeProfileObj.getEmp().getId()) != null) {
                            ep = empPhotoDtlTempRepository
                                    .findByEmpIdAndIsApprovedFalse(employeeProfileObj.getEmp().getId());
                        }

//						if (fileMaster != null) {
                        if (!Objects.equal(empPhotoDtl.getProfileImg(), employeeProfileObj.getProfileImg())) {
                            ep.setProfileImg(empPhotoDtl.getProfileImg());
                        }

                        ep.setEmp(employeeProfileObj.getEmp());
                        ep.setEmpPhotoDtlMstId(employeeProfileObj.getId());
                        ep.setIsApproved(false);

                        ep = empPhotoDtlTempRepository.save(ep);
                        if (ep != null) {
                            res.setStatus("SUCCESS");
                            res.setObj(employeeProfileObj.getEmp().getId());
                        }
//						}else {
//							ep.setProfileImg(employeeProfileObj.getProfileImg());
//							res.setStatus("FAIL");
//						}
//
                    }

                    return res;

                } else {
                    FileMaster fileMaster = null;
                    if (null != employeeProfileObj.getProfileImgFile()
                            && !"".equals(employeeProfileObj.getProfileImgFile().getOriginalFilename())) {
                        String name = employeeProfileObj.getProfileImgFile().getOriginalFilename();
                        fileMaster = commonUtility.saveFileObject(name, employeeProfileObj.getProfileImgFile(),
                                CommonConstant.EMP_FILES, companyId, companyBranchId);
                        if (fileMaster != null) {
                            employeeProfileObj.setProfileImg(fileMaster);

                            File convFile = imageResize.convert(employeeProfileObj.getProfileImgFile());
                            String saveFileName = fileMaster.getFileName();
                            String fileNameWithOutExt = SilverUtil.removeExtension(saveFileName);

                            imageResize.resizeImageUsingThumbnails(fileMaster, convFile,
                                    CommonConstant.IMG_SIZE_FOR_LIST, CommonConstant.IMG_SIZE_FOR_LIST,
                                    CommonConstant.EMP_FILES, companyId, companyBranchId,
                                    fileNameWithOutExt + "_list" + fileMaster.getFileType(),
                                    CommonConstant.IMG_DESC_LIST);

                            imageResize.resizeImageUsingThumbnails(fileMaster, convFile,
                                    CommonConstant.IMG_SIZE_FOR_PROFILE, CommonConstant.IMG_SIZE_FOR_PROFILE,
                                    CommonConstant.EMP_FILES, companyId, companyBranchId,
                                    fileNameWithOutExt + "_profile" + fileMaster.getFileType(),
                                    CommonConstant.IMG_DESC_PROFILE);

                            imageResize.resizeImageUsingThumbnails(fileMaster, convFile,
                                    CommonConstant.IMG_SIZE_FOR_VIEW, CommonConstant.IMG_SIZE_FOR_VIEW,
                                    CommonConstant.EMP_FILES, companyId, companyBranchId,
                                    fileNameWithOutExt + "_view" + fileMaster.getFileType(),
                                    CommonConstant.IMG_DESC_VIEW);
                        }
                    } else {
                        employeeProfileObj.setProfileImg(employeeProfileObj.getProfileImg());
                    }

                    if (um.getRoleMasterId().getIsAdmin()) {
                        EmpPhotoDtl ep = empPhotoDtlRepository.save(employeeProfileObj);
                        if (ep != null) {
                            res.setStatus("SUCCESS");
                            res.setObj(ep.getEmp().getId());
                        }
                    } else {
                        EmpPhotoDtlTemp ep = new EmpPhotoDtlTemp();

//						String name = employeeProfileObj.getProfileImgFile().getOriginalFilename();
//						fileMaster = commonUtility.saveFileObject(name,
//								employeeProfileObj.getProfileImgFile(), CommonConstant.EMP_FILES, companyId,
//								companyBranchId);

                        if (empPhotoDtlTempRepository
                                .findByEmpIdAndIsApprovedFalse(employeeProfileObj.getEmp().getId()) != null) {
                            ep = empPhotoDtlTempRepository
                                    .findByEmpIdAndIsApprovedFalse(employeeProfileObj.getEmp().getId());
                        }

//						if (fileMaster != null) {

                        ep.setProfileImg(fileMaster);
                        ep.setEmp(employeeProfileObj.getEmp());
                        ep.setEmpPhotoDtlMstId(employeeProfileObj.getId());
                        ep.setIsApproved(false);
//							ep.setIsDeletedRecord(false);

                        ep = empPhotoDtlTempRepository.save(ep);
                        if (ep != null) {
                            res.setStatus("SUCCESS");
                            res.setObj(employeeProfileObj.getEmp().getId());
                        }
//						}else {
//							ep.setProfileImg(employeeProfileObj.getProfileImg());
//							res.setStatus("FAIL");
//						}

                    }

                }
            }

//			if (employeeProfileObj.getId() == null) {
//
//				EmpPhotoDtl ep = empPhotoDtlRepository.save(employeeProfileObj);
//				if (ep != null) {
//					res.setStatus("SUCCESS");
//					res.setObj(ep.getEmp().getId());
//				}
//				auditTrailService.saveAuditTrailData("Employee", "Save", "Admin", NotificationModule.EMPLOYEE_PHOTO_DTL,
//						NotificationAction.ADD, "empProfileDtl/save", userId);
//			} else {
//
//				EmpPhotoDtl ep = empPhotoDtlRepository.save(employeeProfileObj);
//				if (ep != null) {
//					res.setStatus("UPDATE");
//					res.setObj(ep.getEmp().getId());
//				}
//				auditTrailService.saveAuditTrailData("Employee", "Update", "Admin", NotificationModule.EMPLOYEE_PHOTO_DTL,
//						NotificationAction.UPDATE, "empProfileDtl/save", userId);
//			}
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update emp profile dtl, " + e.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "/getEmpProfile")
    public @ResponseBody JsonResponse getEmpProfile(@RequestParam("id") Long id, HttpServletRequest request,
                                                    HttpSession session) {
        JsonResponse res = new JsonResponse();
        try {
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            EmpPhotoDtl empPhoto = empPhotoDtlRepository.findByEmpId(id);

            if (empPhoto != null && empPhoto.getProfileImg() != null) {
                File file = new File(environment.getProperty("file.repository.hrms.path") + companyId + File.separator
                        + companyBranchId + File.separator + CommonConstant.EMP_FILES + File.separator
                        + empPhoto.getProfileImg().getFileName());
                if (file != null && file.exists()) {
                    byte[] byteArry = CommonUtility.toByteArray(file);
                    String userProfileImg = javax.xml.bind.DatatypeConverter.printBase64Binary(byteArry);
                    empPhoto.setEmpProfileImgFile(userProfileImg);
                    // model.addAttribute(userProfileImg);
                }
            }

            if (empPhoto != null) {
                res.setStatus("success");
                res.setObj(empPhoto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update emp profile dtl, " + e.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "/editEmpPhoto")
    public @ResponseBody JsonResponse editEmpPhoto(@RequestParam("id") Long id, HttpSession session) {
        logger.info("employee address id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        Optional<EmpPhotoDtl> empPhoto = empPhotoDtlRepository.findById(id);
        if (empPhoto.isPresent()) {

            EmpPhotoDtl empPhotoObj = (EmpPhotoDtl) empPhoto.get();
            res.setObj(empPhotoObj);
            res.setStatus("SUCCESS");
        }

        return res;
    }

    @PostMapping(value = "/empSignDtl/save")
    public @ResponseBody JsonResponse empSignDtl(HttpServletRequest request, HttpSession session,
                                                 @ModelAttribute("employeeProfileObj") EmpPhotoDtl employeeProfileObj, BindingResult result) {
        JsonResponse res = new JsonResponse();
        try {
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

//			System.err.println("---"+employeeProfileObj.getEmp());
//			System.err.println("---"+employeeProfileObj.getEmp().getId());
//			CompanyMasterValidator validatior = new CompanyMasterValidator();
//			validatior.validate(companyMasterObj, result);
//			if (result.hasErrors()) {
//				model.addAttribute("companyMasterObj", companyMasterObj);
//				res.setStatus("FAIL");
//
//				Map<String, String> errors = new HashMap<String, String>();
//				errors = result.getFieldErrors().stream()
//						.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
//				res.setResult(errors);
//				return res;
//			}

            if (employeeProfileObj.getEmp() != null && employeeProfileObj.getEmp().getId() != null) {
                EmpPhotoDtl empPhotoDtl = empPhotoDtlRepository.findByEmpId(employeeProfileObj.getEmp().getId());

                if (empPhotoDtl != null) {

                    if (null != employeeProfileObj.getSignFile()
                            && !"".equals(employeeProfileObj.getSignFile().getOriginalFilename())) {
                        String name = employeeProfileObj.getSignFile().getOriginalFilename();
                        FileMaster fileMaster = commonUtility.saveFileObject(name, employeeProfileObj.getSignFile(),
                                CommonConstant.EMP_FILES, companyId, companyBranchId);
                        if (fileMaster != null) {
                            empPhotoDtl.setSign(fileMaster);
                        }
                    } else {
                        empPhotoDtl.setSign(employeeProfileObj.getSign());
                    }

                    if (um.getRoleMasterId().getIsAdmin()) {

                        EmpPhotoDtl ep = empPhotoDtlRepository.save(empPhotoDtl);
                        if (ep != null) {
                            res.setStatus("SUCCESS");
                            res.setObj(ep.getEmp().getId());
                        }
                    } else {
                        EmpPhotoDtlTemp ep = new EmpPhotoDtlTemp();

                        String name = employeeProfileObj.getSignFile().getOriginalFilename();
                        FileMaster fileMaster = commonUtility.saveFileObject(name, employeeProfileObj.getSignFile(),
                                CommonConstant.EMP_FILES, companyId, companyBranchId);

                        if (empPhotoDtlTempRepository
                                .findByEmpIdAndIsApprovedFalse(employeeProfileObj.getEmp().getId()) != null) {
                            ep = empPhotoDtlTempRepository
                                    .findByEmpIdAndIsApprovedFalse(employeeProfileObj.getEmp().getId());
                        }

                        ep.setEmp(employeeProfileObj.getEmp());
                        ep.setEmpPhotoDtlMstId(employeeProfileObj.getId());
                        ep.setIsApproved(false);

                        if (fileMaster != null) {
                            if (!Objects.equal(empPhotoDtl.getSign(), employeeProfileObj.getSign())) {
                                ep.setSign(fileMaster);
                            }
                            ep = empPhotoDtlTempRepository.save(ep);
                            if (ep != null) {
                                res.setStatus("SUCCESS");
                                res.setObj(employeeProfileObj.getEmp().getId());
                            }
                        } else {
                            res.setStatus("FAIL");
                        }
                    }

                    return res;

                } else {

                    FileMaster fileMaster = null;
                    if (null != employeeProfileObj.getSignFile()
                            && !"".equals(employeeProfileObj.getSignFile().getOriginalFilename())) {
                        String name = employeeProfileObj.getSignFile().getOriginalFilename();
                        fileMaster = commonUtility.saveFileObject(name, employeeProfileObj.getSignFile(),
                                CommonConstant.EMP_FILES, companyId, companyBranchId);
                        if (fileMaster != null) {
                            employeeProfileObj.setSign(fileMaster);
                        }
                    } else {
                        employeeProfileObj.setSign(employeeProfileObj.getSign());
                    }

                    if (um.getRoleMasterId().getIsAdmin()) {
                        EmpPhotoDtl ep = empPhotoDtlRepository.save(employeeProfileObj);
                        if (ep != null) {
                            res.setStatus("SUCCESS");
                            res.setObj(ep.getEmp().getId());
                        }
                    }

                    else {
                        EmpPhotoDtlTemp ep = new EmpPhotoDtlTemp();

                        if (empPhotoDtlTempRepository
                                .findByEmpIdAndIsApprovedFalse(employeeProfileObj.getEmp().getId()) != null) {
                            ep = empPhotoDtlTempRepository
                                    .findByEmpIdAndIsApprovedFalse(employeeProfileObj.getEmp().getId());
                        }

                        String name = employeeProfileObj.getSignFile().getOriginalFilename();
                        fileMaster = commonUtility.saveFileObject(name, employeeProfileObj.getSignFile(),
                                CommonConstant.EMP_FILES, companyId, companyBranchId);

                        ep.setEmp(employeeProfileObj.getEmp());
                        ep.setEmpPhotoDtlMstId(employeeProfileObj.getId());
                        ep.setIsApproved(false);

                        if (fileMaster != null) {

                            ep.setSign(fileMaster);

                            ep = empPhotoDtlTempRepository.save(ep);
                            if (ep != null) {
                                res.setStatus("SUCCESS");
                                res.setObj(employeeProfileObj.getEmp().getId());
                            }
                        } else {
                            res.setStatus("FAIL");
                        }

                    }
                }
            }

//			if (employeeProfileObj.getId() == null) {
//
//				EmpPhotoDtl ep = empPhotoDtlRepository.save(employeeProfileObj);
//				if (ep != null) {
//					res.setStatus("SUCCESS");
//					res.setObj(ep.getEmp().getId());
//				}
//				auditTrailService.saveAuditTrailData("Employee", "Save", "Admin", NotificationModule.EMPLOYEE_PHOTO_DTL,
//						NotificationAction.ADD, "empProfileDtl/save", userId);
//			} else {
//
//				EmpPhotoDtl ep = empPhotoDtlRepository.save(employeeProfileObj);
//				if (ep != null) {
//					res.setStatus("UPDATE");
//					res.setObj(ep.getEmp().getId());
//				}
//				auditTrailService.saveAuditTrailData("Employee", "Update", "Admin", NotificationModule.EMPLOYEE_PHOTO_DTL,
//						NotificationAction.UPDATE, "empProfileDtl/save", userId);
//			}
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update emp profile dtl, " + e.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "/getEmpSign")
    public @ResponseBody JsonResponse getEmpSign(@RequestParam("id") Long id, HttpServletRequest request,
                                                 HttpSession session) {
        JsonResponse res = new JsonResponse();
        try {
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            EmpPhotoDtl empPhoto = empPhotoDtlRepository.findByEmpId(id);

            if (empPhoto != null && empPhoto.getSign() != null) {
                File file = new File(environment.getProperty("file.repository.hrms.path") + companyId + File.separator
                        + companyBranchId + File.separator + CommonConstant.EMP_FILES + File.separator
                        + empPhoto.getSign().getFileName());
                if (file != null && file.exists()) {
                    byte[] byteArry = CommonUtility.toByteArray(file);
                    String userProfileImg = javax.xml.bind.DatatypeConverter.printBase64Binary(byteArry);
                    empPhoto.setSignImgFile(userProfileImg);
                }
            }

            if (empPhoto != null) {
                res.setStatus("success");
                res.setObj(empPhoto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update emp profile dtl, " + e.getMessage());
        }
        return res;
    }

    @RequestMapping(value = "/editSign")
    public @ResponseBody JsonResponse editSign(@RequestParam("id") Long id, HttpSession session) {
        logger.info("employee address id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        Optional<EmpPhotoDtl> empPhoto = empPhotoDtlRepository.findById(id);
        if (empPhoto.isPresent()) {

            EmpPhotoDtl empPhotoObj = (EmpPhotoDtl) empPhoto.get();
            res.setObj(empPhotoObj);
            res.setStatus("SUCCESS");
        }

        return res;
    }

    @GetMapping(value = "/deleteEmpProfileImg/{id}")
    public @ResponseBody JsonResponse deleteEmpProfileImg(@PathVariable("id") Long id, Model model,
                                                          HttpServletRequest request, HttpSession session, @RequestParam("isTempData") Boolean isTempData) {
        logger.info("EmployeeController.deleteEmpProfileImg");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            if (um != null) {
                Optional<EmpPhotoDtl> photoDtl = empPhotoDtlRepository.findById(id);

                if (um.getRoleMasterId().getIsAdmin()) {
                    if (photoDtl.isPresent()) {

                        photoDtl.get().setProfileImg(null);
                        empPhotoDtlRepository.save(photoDtl.get());
                        res.setObj(photoDtl);
                        res.setStatus("SUCCESS");
                    } else {
                        res.setStatus("FAIL");
                    }
                } else {

                    if (isTempData) {

//						empPhotoDtlTempRepository.deleteById(id);
                        Optional<EmpPhotoDtlTemp> photoDtlTemp = empPhotoDtlTempRepository.findById(id);
                        if (photoDtlTemp.isPresent()) {

                            photoDtlTemp.get().setProfileImg(null);
                            empPhotoDtlTempRepository.save(photoDtlTemp.get());
                            res.setObj(photoDtlTemp);
                            res.setStatus("SUCCESS");
                        } else {
                            res.setStatus("FAIL");
                        }

//						res.setStatus("SUCCESS");
                    } else {
                        EmpPhotoDtlTemp photoDtlTemp = new EmpPhotoDtlTemp();

//						if(empPhotoDtlTempRepository.findByEmpPhotoDtlMstIdAndIsApprovedFalse(id).isPresent()){
//							photoDtlTemp = empPhotoDtlTempRepository.findByEmpPhotoDtlMstIdAndIsApprovedFalse(id).get();
//						}
//						photoDtlTemp.setIsDeletedRecord(true);
//						photoDtlTemp.setEmpPhotoDtlMstId(id);
//						photoDtlTemp.setIsApproved(false);
//						photoDtlTemp.setEmp(photoDtl.get().getEmp());
//						photoDtlTemp.setProfileImg(null);

                        if (empPhotoDtlTempRepository.findByEmpPhotoDtlMstIdAndIsApprovedFalse(id)
                                .isPresent()) {
                            photoDtlTemp = empPhotoDtlTempRepository
                                    .findByEmpPhotoDtlMstIdAndIsApprovedFalse(id).get();

                            if (photoDtlTemp.getSign() != null) {

                                if(empPhotoDtlTempRepository.findByEmpPhotoDtlMstIdAndIsApprovedFalse(id).get().getSign() != null) {
                                    photoDtlTemp.setSign(photoDtlTemp.getSign());
                                }
                                else {
                                    photoDtlTemp.setSign(photoDtl.get().getSign());
                                }

                            }
                            photoDtlTemp.setProfileImg(null);
                            photoDtlTemp.setIsDeletedRecord(true);

                        } else {
                            photoDtlTemp.setIsDeletedRecord(true);
                            photoDtlTemp.setEmpPhotoDtlMstId(id);
                            photoDtlTemp.setIsApproved(false);
                            photoDtlTemp.setEmp(photoDtl.get().getEmp());
                            photoDtlTemp.setProfileImg(null);
                            photoDtlTemp.setSign(photoDtl.get().getSign());
                        }

//						photoDtlTemp.setSign(photoDtl.get().getSign());

                        EmpPhotoDtlTemp em = empPhotoDtlTempRepository.save(photoDtlTemp);

                        if (em != null) {
                            res.setStatus("SUCCESS");
                        }
                    }

                }

                auditTrailService.saveAuditTrailData("Employee", "Delete", "Admin",
                        NotificationModule.EMPLOYEE_PHOTO_DTL, NotificationAction.DELETE, "/deleteEmpProfileImg",
                        userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus("FAIL");
        }

        return res;
    }

    @GetMapping(value = "/deleteSignImg/{id}")
    public @ResponseBody JsonResponse deleteSignImg(@PathVariable("id") Long id, Model model,
                                                    HttpServletRequest request, HttpSession session, @RequestParam("isTempData") Boolean isTempData) {
        logger.info("EmployeeController.deleteSignImg");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            if (um != null) {
                Optional<EmpPhotoDtl> photoDtl = empPhotoDtlRepository.findById(id);

                if (um.getRoleMasterId().getIsAdmin()) {
                    if (photoDtl.isPresent()) {

                        photoDtl.get().setSign(null);
                        empPhotoDtlRepository.save(photoDtl.get());
                        res.setObj(photoDtl);
                        res.setStatus("SUCCESS");
                    } else {
                        res.setStatus("FAIL");
                    }
                } else {

                    if (isTempData) {

//						empPhotoDtlTempRepository.deleteById(id);
                        Optional<EmpPhotoDtlTemp> photoDtlTemp = empPhotoDtlTempRepository.findById(id);
                        if (photoDtlTemp.isPresent()) {

                            photoDtlTemp.get().setSign(null);
                            empPhotoDtlTempRepository.save(photoDtlTemp.get());
                            res.setObj(photoDtlTemp);
                            res.setStatus("SUCCESS");
                        } else {
                            res.setStatus("FAIL");
                        }

//						res.setStatus("SUCCESS");
                    } else {
                        EmpPhotoDtlTemp photoDtlTemp = new EmpPhotoDtlTemp();

//						if(empPhotoDtlTempRepository.findByEmpPhotoDtlMstIdAndIsApprovedFalse(id).isPresent()){
//							photoDtlTemp = empPhotoDtlTempRepository.findByEmpPhotoDtlMstIdAndIsApprovedFalse(id).get();
//						}
//						photoDtlTemp.setIsDeletedRecord(true);
//						photoDtlTemp.setEmpPhotoDtlMstId(id);
//						photoDtlTemp.setIsApproved(false);
//						photoDtlTemp.setEmp(photoDtl.get().getEmp());
//						photoDtlTemp.setProfileImg(photoDtl.get().getProfileImg());

//						photoDtlTemp.setSign(null);

                        if (empPhotoDtlTempRepository.findByEmpPhotoDtlMstIdAndIsApprovedFalse(id)
                                .isPresent()) {
                            photoDtlTemp = empPhotoDtlTempRepository
                                    .findByEmpPhotoDtlMstIdAndIsApprovedFalse(id).get();

                            if (photoDtlTemp.getProfileImg() != null) {

                                if(empPhotoDtlTempRepository.findByEmpPhotoDtlMstIdAndIsApprovedFalse(id).get().getProfileImg() != null) {
                                    photoDtlTemp.setProfileImg(photoDtlTemp.getProfileImg());
                                }
                                else {
                                    photoDtlTemp.setProfileImg(photoDtl.get().getProfileImg());
                                }

                            }
                            photoDtlTemp.setSign(null);
                            photoDtlTemp.setIsDeletedRecord(true);

                        } else {
                            photoDtlTemp.setIsDeletedRecord(true);
                            photoDtlTemp.setEmpPhotoDtlMstId(id);
                            photoDtlTemp.setIsApproved(false);
                            photoDtlTemp.setEmp(photoDtl.get().getEmp());
                            photoDtlTemp.setSign(null);
                            photoDtlTemp.setProfileImg(photoDtl.get().getProfileImg());
                        }

                        EmpPhotoDtlTemp em = empPhotoDtlTempRepository.save(photoDtlTemp);

                        if (em != null) {
                            res.setStatus("SUCCESS");
                        }
                    }

                }

                auditTrailService.saveAuditTrailData("Employee", "Delete", "Admin",
                        NotificationModule.EMPLOYEE_PHOTO_DTL, NotificationAction.DELETE, "/deleteSignImg", userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus("FAIL");
        }

        return res;
    }

    @PostMapping(value = "/saveNomineeInfo")
    public @ResponseBody JsonResponse saveNomineeInfo(HttpServletRequest request, HttpSession session, Model model,
                                                      @ModelAttribute("nomineeObj") Nominee nomineeObj, BindingResult result) {
        logger.info("saveNomineeInfo" + nomineeObj.toString());
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                res.setStatus("FAIL");
                return res;
            }

            if (companyId != null) {
                Optional<CompanyMaster> cm = companyMasterRepository.findById(companyId);
                if (cm.isPresent()) {
                    nomineeObj.setCompany(cm.get());
                }
            }
            if (companyBranchId != null) {
                Optional<CompanyBranchMaster> cbm = companyBranchMasterRepository.findById(companyBranchId);
                if (cbm.isPresent()) {
                    nomineeObj.setCompanyBranch(cbm.get());
                }
            }

            String nomineeFirstName = request.getParameter("nomineeFirstName");
            if (StringUtil.isNotEmpty(nomineeFirstName)) {
                nomineeObj.setNomineeFirstName(nomineeFirstName);
            }

            String nomineeMiddleName = request.getParameter("nomineeMiddleName");
            if (StringUtil.isNotEmpty(nomineeMiddleName)) {
                nomineeObj.setNomineeMiddleName(nomineeMiddleName);
            }

            String nomineeLastName = request.getParameter("nomineeLastName");
            if (StringUtil.isNotEmpty(nomineeLastName)) {
                nomineeObj.setNomineeLastName(nomineeLastName);
            }

//			JobInfoValidator validatior = new JobInfoValidator();
//			validatior.validate(jobObj, result);
//
//			if (result.hasErrors()) {
//				model.addAttribute("jobObj", jobObj);
//
//				res.setStatus("FAIL");
//
//				Map<String, String> errors = new HashMap<String, String>();
//				errors = result.getFieldErrors().stream()
//						.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
//				res.setResult(errors);
//				return res;
//			}

            if (nomineeObj.getCountry() == null || nomineeObj.getCountry().getId() == null) {
                nomineeObj.setCountry(null);
            }

            if (nomineeObj.getState() == null || nomineeObj.getState().getId() == null) {
                nomineeObj.setState(null);
            }

            if (nomineeObj.getDistrict() == null || nomineeObj.getDistrict().getId() == null) {
                nomineeObj.setDistrict(null);
            }

            if (nomineeObj.getId() == null) {
                if (StringUtil.isNotEmpty(request.getParameter("nomineefamilyId"))) {
                    EmpFamilyDtl empFamily = empFamilyRepository
                            .findById(Long.parseLong(request.getParameter("nomineefamilyId"))).orElse(null);
                    nomineeObj.setEmpFamily(empFamily);
                } else {
                    nomineeObj.setEmpFamily(null);
                }
                if (StringUtil.isNotEmpty(request.getParameter("nomineerelationid"))) {

                    FamilyRelationMaster familyRelation = familyRelationMasterService
                            .findById(Long.parseLong(request.getParameter("nomineerelationid")));
                    nomineeObj.setFamilyRelationMaster(familyRelation);
                } else {
                    System.out.println("request.getParameter(\"nomineerelation\") "
                            + nomineeObj.getFamilyRelationMaster().getId());
                    if (nomineeObj.getFamilyRelationMaster() != null) {
                        FamilyRelationMaster familyRelation = familyRelationMasterService
                                .findById(nomineeObj.getFamilyRelationMaster().getId());
                        nomineeObj.setFamilyRelationMaster(familyRelation);
                    } else {
                        nomineeObj.setFamilyRelationMaster(null);
                    }
                }
                nomineeObj.setGender(request.getParameter("gender"));
                nomineeObj.setDateOfBirth(DateUtil.convertStringToDate(request.getParameter("nomineeBirthdate"),
                        DateUtil.IST_DATE_FORMATE));
                String[] nomineeTypeIds = request.getParameterValues("bulkActionCheckBox");
                String s1 = StringUtils.join(nomineeTypeIds, ",");
                nomineeObj.setNomineeType(s1);
                String[] percentage = request.getParameterValues("percentage");
                String percentages = StringUtils.join(percentage, ",");
                nomineeObj.setPercentage(percentages);

                if (um.getRoleMasterId().getIsAdmin()) {
                    Nominee nominee = nomineeService.save(nomineeObj);
                    if (nominee != null) {
                        Nominee nomineeobj = (Nominee) nominee;
                        res.setObj(nomineeobj);
                        res.setStatus("SUCCESS");
                    }
                } else {
                    NomineeTemp nomineeTemp = new NomineeTemp();

                    nomineeTemp.setAddress(nomineeObj.getAddress());
                    nomineeTemp.setCompany(nomineeObj.getCompany());
                    nomineeTemp.setCompanyBranch(nomineeObj.getCompanyBranch());
                    nomineeTemp.setContactNumber(nomineeObj.getContactNumber());
                    nomineeTemp.setCountry(nomineeObj.getCountry());
                    nomineeTemp.setCreatedBy(userId);
                    nomineeTemp.setCreatedDate(new Date());
                    nomineeTemp.setDistrict(nomineeObj.getDistrict());
                    nomineeTemp.setDateOfBirth(nomineeObj.getDateOfBirth());
                    nomineeTemp.setEmp(nomineeObj.getEmp());
//					if()
                    if (nomineeObj.getEmpFamily() != null) {
                        nomineeTemp.setEmpFamily(nomineeObj.getEmpFamily().getId());
                    }
                    nomineeTemp.setFamilyRelationMaster(nomineeObj.getFamilyRelationMaster());
                    nomineeTemp.setGender(nomineeObj.getGender());
                    nomineeTemp.setIpAddress(request.getRemoteAddr());
                    nomineeTemp.setNomineeFirstName(nomineeFirstName);
                    nomineeTemp.setNomineeType(s1);
                    nomineeTemp.setPercentage(percentages);
                    nomineeTemp.setState(nomineeObj.getState());
                    nomineeTemp.setNomineeLastName(nomineeLastName);
                    nomineeTemp.setNomineeMiddleName(nomineeMiddleName);
                    nomineeTemp.setPanNumber(nomineeObj.getPanNumber());
                    nomineeTemp.setPincode(nomineeObj.getPincode());
                    nomineeTemp.setUidNo(nomineeObj.getUidNo());
                    nomineeTemp.setPriority(nomineeObj.getPriority());
                    nomineeTemp.setNomineeName(nomineeObj.getNomineeName());
                    nomineeTemp.setNomineeInvalidCondition(nomineeObj.getNomineeInvalidCondition());

                    NomineeTemp nt = nomineeTempRepository.save(nomineeTemp);

                    if (nt != null) {
                        res.setObj(nt);
                        res.setStatus("SUCCESS");
                    }
                }

                auditTrailService.saveAuditTrailData("Employee Nominee Details", "Save", "Admin",
                        NotificationModule.EMPLOYEE_NOMINEE_DETAILS, NotificationAction.ADD, "/save", userId);
            } else {
                if (StringUtil.isNotEmpty(request.getParameter("nomineefamilyId"))) {
                    EmpFamilyDtl empFamily = empFamilyRepository
                            .findById(Long.parseLong(request.getParameter("nomineefamilyId"))).orElse(null);
                    nomineeObj.setEmpFamily(empFamily);
                } else {
                    nomineeObj.setEmpFamily(null);
                }

                if (StringUtil.isNotEmpty(request.getParameter("nomineerelationid"))) {

                    FamilyRelationMaster familyRelation = familyRelationMasterService
                            .findById(Long.parseLong(request.getParameter("nomineerelationid")));
                    nomineeObj.setFamilyRelationMaster(familyRelation);
                } else {
                    System.out.println("request.getParameter(\"nomineerelation\") "
                            + nomineeObj.getFamilyRelationMaster().getId());
                    if (nomineeObj.getFamilyRelationMaster() != null) {
                        FamilyRelationMaster familyRelation = familyRelationMasterService
                                .findById(nomineeObj.getFamilyRelationMaster().getId());
                        nomineeObj.setFamilyRelationMaster(familyRelation);
                    } else {
                        nomineeObj.setFamilyRelationMaster(null);
                    }
                }

                nomineeObj.setGender(request.getParameter("gender"));
                if (StringUtil.isNotEmpty(request.getParameter("nomineeBirthdate"))) {
                    nomineeObj.setDateOfBirth(DateUtil.convertStringToDate(request.getParameter("nomineeBirthdate"),
                            DateUtil.IST_DATE_FORMATE));
                } else {
                    nomineeObj.setDateOfBirth(DateUtil.convertStringToDate(request.getParameter("dateOfBirthdate"),
                            DateUtil.IST_DATE_FORMATE));
                }
                String[] nomineeTypeIds = request.getParameterValues("editbulkActionCheckBox");
                String s1 = StringUtils.join(nomineeTypeIds, ",");
                nomineeObj.setNomineeType(s1);
                String[] percentage = request.getParameterValues("editpercentage");
                String percentages = StringUtils.join(percentage, ",");
                nomineeObj.setPercentage(percentages);

                if (um.getRoleMasterId().getIsAdmin()) {
                    Nominee nominee = nomineeService.save(nomineeObj);
                    if (nominee != null) {
                        List<NomineeTypeMaster> nomineeTypeMasterList = nomineeTypeMasterRepo
                                .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                        CommonConstant.ACTIVE, companyId, companyBranchId);
                        Nominee nomineeobj = (Nominee) nominee;
                        res.setObj(nomineeobj);
                        res.setNomineeTypeList(nomineeTypeMasterList);
                        res.setStatus("UPDATE");
                    }
                } else {
                    if (!request.getParameter("isTempNomineeDetails").trim().isEmpty()) {
                        NomineeTemp nomineeTemp = new NomineeTemp();

                        if (nomineeTempRepository.findById(nomineeObj.getId()).isPresent()) {
                            nomineeTemp = nomineeTempRepository.findById(nomineeObj.getId()).get();
                        }

                        nomineeTemp.setCompany(nomineeObj.getCompany());
                        nomineeTemp.setCompanyBranch(nomineeObj.getCompanyBranch());
                        nomineeTemp.setCreatedBy(userId);
                        nomineeTemp.setCreatedDate(new Date());
                        nomineeTemp.setUpdatedBy(userId);
                        nomineeTemp.setUpdatedDate(new Date());
                        nomineeTemp.setIpAddress(request.getRemoteAddr());
                        nomineeTemp.setEmp(nomineeObj.getEmp());

//						if()
                        if (nomineeObj.getEmpFamily() != null) {
                            nomineeTemp.setEmpFamily(nomineeObj.getEmpFamily().getId());
                        }

                        nomineeTemp.setContactNumber(nomineeObj.getContactNumber());
                        nomineeTemp.setCountry(nomineeObj.getCountry());
                        nomineeTemp.setDistrict(nomineeObj.getDistrict());
                        nomineeTemp.setDateOfBirth(nomineeObj.getDateOfBirth());
                        nomineeTemp.setEmp(nomineeObj.getEmp());
                        nomineeTemp.setAddress(nomineeObj.getAddress());
                        nomineeTemp.setFamilyRelationMaster(nomineeObj.getFamilyRelationMaster());
                        nomineeTemp.setGender(nomineeObj.getGender());
                        nomineeTemp.setNomineeType(s1);
                        nomineeTemp.setPercentage(percentages);
                        nomineeTemp.setState(nomineeObj.getState());
                        nomineeTemp.setNomineeLastName(nomineeLastName);
                        nomineeTemp.setNomineeMiddleName(nomineeMiddleName);
                        nomineeTemp.setPanNumber(nomineeObj.getPanNumber());
                        nomineeTemp.setPincode(nomineeObj.getPincode());
                        nomineeTemp.setUidNo(nomineeObj.getUidNo());
                        nomineeTemp.setPriority(nomineeObj.getPriority());
                        nomineeTemp.setNomineeName(nomineeObj.getNomineeName());
                        nomineeTemp.setNomineeInvalidCondition(nomineeObj.getNomineeInvalidCondition());

                        NomineeTemp nt = nomineeTempRepository.save(nomineeTemp);

                        if (nt != null) {
                            res.setObj(nt);
                            res.setStatus("UPDATE");
                        }
                    } else {
                        NomineeTemp nomineeTemp = new NomineeTemp();

                        Nominee nominee = nomineeRepo.findById(nomineeObj.getId()).orElse(null);

                        if (nomineeTempRepository.findAllByEmpNomineeMstIdAndIsApprovedFalse(nomineeObj.getId())
                                .isPresent()) {
                            nomineeTemp = nomineeTempRepository
                                    .findAllByEmpNomineeMstIdAndIsApprovedFalse(nomineeObj.getId()).get();
                            ;
                        }
                        nomineeTemp.setCompany(nomineeObj.getCompany());
                        nomineeTemp.setCompanyBranch(nomineeObj.getCompanyBranch());
                        nomineeTemp.setCreatedBy(userId);
                        nomineeTemp.setCreatedDate(new Date());
                        nomineeTemp.setUpdatedBy(userId);
                        nomineeTemp.setUpdatedDate(new Date());
                        nomineeTemp.setIpAddress(request.getRemoteAddr());
                        nomineeTemp.setEmp(nomineeObj.getEmp());
                        nomineeTemp.setEmpNomineeMstId(nominee.getId());
                        nomineeTemp.setIsEditedRecord(true);
                        nomineeTemp.setIsDeletedRecord(false);

                        if (!Objects.equal(nomineeObj.getAddress() != null ? nomineeObj.getAddress() : "",
                                nominee.getAddress() != null ? nominee.getAddress() : ""))
                            nomineeTemp.setAddress(nomineeObj.getAddress());

                        if (!Objects.equal(nomineeObj.getContactNumber() != null ? nomineeObj.getContactNumber() : "",
                                nominee.getContactNumber() != null ? nominee.getContactNumber() : ""))
                            nomineeTemp.setContactNumber(nomineeObj.getContactNumber());

                        if (!Objects.equal(nomineeObj.getCountry() != null ? nomineeObj.getCountry() : "",
                                nominee.getCountry() != null ? nominee.getCountry() : ""))
                            nomineeTemp.setCountry(nomineeObj.getCountry());

                        if (!Objects.equal(nomineeObj.getDistrict() != null ? nomineeObj.getDistrict() : "",
                                nominee.getDistrict() != null ? nominee.getDistrict() : ""))
                            nomineeTemp.setDistrict(nomineeObj.getDistrict());

                        if (!Objects.equal(nomineeObj.getDateOfBirth() != null ? nomineeObj.getDateOfBirth() : "",
                                nominee.getDateOfBirth() != null ? nominee.getDateOfBirth() : ""))
                            nomineeTemp.setDateOfBirth(nomineeObj.getDateOfBirth());

                        if (!Objects.equal(nomineeObj.getContactNumber() != null ? nomineeObj.getContactNumber() : "",
                                nominee.getContactNumber() != null ? nominee.getContactNumber() : ""))
                            nomineeTemp.setContactNumber(nomineeObj.getContactNumber());

                        if (!Objects.equal(
                                nomineeObj.getFamilyRelationMaster() != null ? nomineeObj.getFamilyRelationMaster()
                                        : "",
                                nominee.getFamilyRelationMaster() != null ? nominee.getFamilyRelationMaster() : ""))
                            nomineeTemp.setFamilyRelationMaster(nomineeObj.getFamilyRelationMaster());

                        if (!Objects.equal(nomineeObj.getGender() != null ? nomineeObj.getGender() : "",
                                nominee.getGender() != null ? nominee.getGender() : ""))
                            nomineeTemp.setGender(nomineeObj.getGender());

                        if (!Objects.equal(
                                nomineeObj.getNomineeFirstName() != null ? nomineeObj.getNomineeFirstName() : "",
                                nominee.getNomineeFirstName() != null ? nominee.getNomineeFirstName() : ""))
                            nomineeTemp.setNomineeFirstName(nomineeFirstName);

                        if (!Objects.equal(
                                nomineeObj.getNomineeMiddleName() != null ? nomineeObj.getNomineeMiddleName() : "",
                                nominee.getNomineeMiddleName() != null ? nominee.getNomineeMiddleName() : ""))
                            nomineeTemp.setNomineeMiddleName(nomineeMiddleName);

                        if (!Objects.equal(nomineeObj.getNomineeType() != null ? nomineeObj.getNomineeType() : "",
                                nominee.getNomineeType() != null ? nominee.getNomineeType() : ""))
                            nomineeTemp.setNomineeType(s1);

                        if (!Objects.equal(nomineeObj.getPercentage() != null ? nomineeObj.getPercentage() : "",
                                nominee.getPercentage() != null ? nominee.getPercentage() : ""))
                            nomineeTemp.setPercentage(percentages);

                        if (!Objects.equal(nomineeObj.getState() != null ? nomineeObj.getState() : "",
                                nominee.getState() != null ? nominee.getState() : ""))
                            nomineeTemp.setState(nomineeObj.getState());

                        if (!Objects.equal(
                                nomineeObj.getNomineeLastName() != null ? nomineeObj.getNomineeLastName() : "",
                                nominee.getNomineeLastName() != null ? nominee.getNomineeLastName() : ""))
                            nomineeTemp.setNomineeLastName(nomineeLastName);

                        if (!Objects.equal(nomineeObj.getPanNumber() != null ? nomineeObj.getPanNumber() : "",
                                nominee.getPanNumber() != null ? nominee.getPanNumber() : ""))
                            nomineeTemp.setPanNumber(nomineeObj.getPanNumber());

                        if (!Objects.equal(nomineeObj.getPincode() != null ? nomineeObj.getPincode() : "",
                                nominee.getPincode() != null ? nominee.getPincode() : ""))
                            nomineeTemp.setPincode(nomineeObj.getPincode());

                        if (!Objects.equal(nomineeObj.getUidNo() != null ? nomineeObj.getUidNo() : "",
                                nominee.getUidNo() != null ? nominee.getUidNo() : ""))
                            nomineeTemp.setUidNo(nomineeObj.getUidNo());

                        if (!Objects.equal(nomineeObj.getPriority() != null ? nomineeObj.getPriority() : "",
                                nominee.getPriority() != null ? nominee.getPriority() : ""))
                            nomineeTemp.setPriority(nomineeObj.getPriority());

                        if (!Objects.equal(nomineeObj.getNomineeName() != null ? nomineeObj.getNomineeName() : "",
                                nominee.getNomineeName() != null ? nominee.getNomineeName() : ""))
                            nomineeTemp.setNomineeName(nomineeObj.getNomineeName());

                        if (!Objects.equal(
                                nomineeObj.getNomineeInvalidCondition() != null
                                        ? nomineeObj.getNomineeInvalidCondition()
                                        : "",
                                nominee.getNomineeInvalidCondition() != null ? nominee.getNomineeInvalidCondition()
                                        : ""))
                            nomineeTemp.setNomineeInvalidCondition(nomineeObj.getNomineeInvalidCondition());

//						if()
//						nomineeTemp.setEmpFamily(nomineeObj.getEmpFamily().getId());

                        NomineeTemp nt = nomineeTempRepository.save(nomineeTemp);

                        if (nt != null) {
                            res.setObj(nt);
                            res.setStatus("UPDATE");
                        }
                    }
                }

                auditTrailService.saveAuditTrailData("Employee Nominee Details", "Update", "Admin",
                        NotificationModule.EMPLOYEE_NOMINEE_DETAILS, NotificationAction.UPDATE, "/save", userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in save or update employee Nominee Details, " + e.getMessage());
        }
        return res;
    }

    @RequestMapping("/nomineeNameExist")
    @ResponseBody
    public String nomineeNameExist(@RequestParam(value = "nomineeFirstName") String nomineeFirstName,
                                   @RequestParam(value = "emp") String emp, HttpServletRequest request, Model model,
                                   HttpServletResponse response, HttpSession session) throws JSONException {

        try {

//			logger.info("WorkflowRoleMasterController:workflowRoleNameExist");

            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                return "redirect:/signin";
            } else {

                CompanyMaster cm = companyMasterRepository.findById(companyId).orElse(null);
                CompanyBranchMaster cbm = companyBranchMasterRepository.findById(companyBranchId).orElse(null);

                List<Nominee> nomineeMaster = nomineeRepo
                        .findAllByCompanyAndCompanyBranchAndNomineeFirstNameIgnoreCaseAndEmp(cm, cbm, nomineeFirstName,
                                employeeRepository.findById(Long.parseLong(emp)).orElse(null));
                System.err.println(nomineeMaster.size());

                List<NomineeTemp> nomineeTemps = nomineeTempRepository
                        .findAllByCompanyAndCompanyBranchAndNomineeFirstNameIgnoreCaseAndEmpAndIsApprovedFalseAndIsDeletedRecordFalse(
                                cm, cbm, nomineeFirstName,
                                employeeRepository.findById(Long.parseLong(emp)).orElse(null));

                if (nomineeMaster.size() == 0 && nomineeTemps.size() == 0) {
                    return "SUCCESS";
                } else {
                    return "ERROR";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in Workflow Role Delete");
            return "ERROR";
        }

    }

    @RequestMapping(value = "/getNomineeList/{id}")
    public @ResponseBody JsonResponse getNomineeList(@PathVariable("id") Long id, Model model,
                                                     HttpServletRequest request, HttpSession session) {

        JsonResponse res = new JsonResponse();
        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Employee emp = employeeRepository.findById(id).orElse(null);
        List<Nominee> empNomineeList = nomineeRepo.findByEmpOrderByPriority(emp);
        List<Object> empNMList = new ArrayList<Object>();
        empNMList.addAll(empNomineeList);
        if (empNMList.size() > 0) {
            res.setStatus("success");
            res.setObjList(empNMList);
        } else {

            if (!um.getRoleMasterId().getIsAdmin()) {
                List<NomineeTemp> nomineeTemps = nomineeTempRepository
                        .findAllByEmpIdAndIsApprovedFalseAndIsEditedRecordFalseAndIsDeletedRecordFalse(emp.getId());

                List<Object> nomineeTemp = new ArrayList<Object>();
                nomineeTemp.addAll(nomineeTemps);
                if (nomineeTemp.size() > 0) {
                    res.setStatus("success");
                    res.setObjList(nomineeTemp);
                    res.setTempTableData(true);
                }
            }
        }
        return res;
    }

    @RequestMapping(value = "/editEmpNominee")
    public @ResponseBody JsonResponse editEmpNominee(@RequestParam("id") Long id, HttpSession session,
                                                     @RequestParam("isTempData") Boolean isTempData) {
        logger.info("employee address id " + id);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        if (um.getRoleMasterId().getIsAdmin()) {
            Nominee nominee = nomineeService.findById(id);
            if (nominee != null) {
                List<NomineeTypeMaster> nomineeTypeMasterList = nomineeTypeMasterRepo
                        .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                CommonConstant.ACTIVE, companyId, companyBranchId);
                res.setObj(nominee);
                res.setNomineeTypeList(nomineeTypeMasterList);
                res.setStatus("SUCCESS");
            }
        } else {
            if (isTempData) {
                if (nomineeTempRepository.findById(id).isPresent()) {
                    NomineeTemp nomineeTemp = nomineeTempRepository.findById(id).get();

                    List<NomineeTypeMaster> nomineeTypeMasterList = nomineeTypeMasterRepo
                            .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                    CommonConstant.ACTIVE, companyId, companyBranchId);
                    res.setObj(nomineeTemp);
                    res.setNomineeTypeList(nomineeTypeMasterList);
                    res.setStatus("SUCCESS");
                }
            } else {
                Nominee nominee = nomineeService.findById(id);
                if (nominee != null) {
                    List<NomineeTypeMaster> nomineeTypeMasterList = nomineeTypeMasterRepo
                            .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                    CommonConstant.ACTIVE, companyId, companyBranchId);
                    res.setObj(nominee);
                    res.setNomineeTypeList(nomineeTypeMasterList);
                    res.setStatus("SUCCESS");
                }
            }

        }

        return res;
    }

    @GetMapping(value = "/deleteNomineeDtl/{id}")
    public @ResponseBody JsonResponse deleteNomineeDtl(@PathVariable("id") Long id, Model model,
                                                       HttpServletRequest request, HttpSession session, @RequestParam("isTempData") Boolean isTempData) {
        logger.info("EmployeeController.deleteNomineeDtl");
        JsonResponse res = new JsonResponse();
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            if (um != null) {
                if (um.getRoleMasterId().getIsAdmin()) {
                    Nominee nominee = nomineeService.findById(id);
                    if (nominee != null) {

                        List<Nominee> empNomineeList = nomineeRepo.findByEmpOrderByPriority(nominee.getEmp());
                        List<Object> empNMList = new ArrayList<Object>();
                        empNMList.addAll(empNomineeList);
                        if (empNMList.size() > 0) {
                            res.setObjList(empNMList);
                        }

                        nomineeService.deleteById(id);

                        if (nomineeTempRepository.findAllByEmpNomineeMstIdAndIsApprovedFalse(id).isPresent()) {

                            NomineeTemp nomineeTemp = nomineeTempRepository
                                    .findAllByEmpNomineeMstIdAndIsApprovedFalse(id).get();

                            nomineeTempRepository.deleteById(nomineeTemp.getId());

                        }
                        res.setStatus("SUCCESS");
                    } else {
                        res.setStatus("FAIL");
                    }
                } else {
                    if (isTempData) {
                        nomineeTempRepository.deleteById(id);
                        res.setStatus("SUCCESS");
                    } else {
                        Nominee nominee = nomineeService.findById(id);
                        // here they got all information of delete nominee


                        NomineeTemp nomineeTemp = new NomineeTemp();

                        NomineeHistory nomineeHistory = new NomineeHistory();

                        if (nomineeTempRepository.findAllByEmpNomineeMstIdAndIsApprovedFalse(nominee.getId())
                                .isPresent()) {
                            nomineeTemp = nomineeTempRepository
                                    .findAllByEmpNomineeMstIdAndIsApprovedFalse(nominee.getId()).get();
                        }


                        nomineeTemp.setCompany(nominee.getCompany());
                        nomineeTemp.setCompanyBranch(nominee.getCompanyBranch());
                        nomineeTemp.setCreatedBy(userId);
                        nomineeTemp.setCreatedDate(new Date());
                        nomineeTemp.setUpdatedBy(userId);
                        nomineeTemp.setUpdatedDate(new Date());
                        nomineeTemp.setIpAddress(request.getRemoteAddr());
                        nomineeTemp.setEmp(nominee.getEmp());
                        nomineeTemp.setIsEditedRecord(false);
                        nomineeTemp.setIsDeletedRecord(true);
                        nomineeTemp.setEmpNomineeMstId(nominee.getId());

                        NomineeTemp nt = nomineeTempRepository.save(nomineeTemp);



                        nomineeHistory.setEmpNomineeMstId(nt.getId());
                        nomineeHistory.setEmp(nominee.getEmp());
                        nomineeHistory.setCompany(nominee.getCompany());
                        nomineeHistory.setCompanyBranch(nominee.getCompanyBranch());
                        nomineeHistory.setCreatedBy(userId);
                        nomineeHistory.setCreatedDate(new Date());
                        nomineeHistory.setUpdatedBy(userId);
                        nomineeHistory.setUpdatedDate(new Date());
                        nomineeHistory.setIpAddress(request.getRemoteAddr());
                        nomineeHistory.setNomineeFirstName(nominee.getNomineeFirstName());
                        nomineeHistory.setNomineeMiddleName(nominee.getNomineeMiddleName());
                        nomineeHistory.setNomineeLastName(nominee.getNomineeLastName());
                        nomineeHistory.setAddress(nominee.getAddress());
                        nomineeHistory.setContactNumber(nominee.getContactNumber());
                        nomineeHistory.setDateOfBirth(nominee.getDateOfBirth());
                        nomineeHistory.setFamilyRelationMaster(nominee.getFamilyRelationMaster());
                        nomineeHistory.setPriority(nominee.getPriority());
                        nomineeHistory.setUidNo(nominee.getUidNo());
                        nomineeHistory.setPercentage(nominee.getPercentage());

                        nomineeHistory.setIsEditedRecord(false);
                        nomineeHistory.setIsDeletedRecord(true);


                        nomineeHistoryRepository.save(nomineeHistory);




                        System.out.println("Here NT===========================================>"+nt);
                        if (nt != null) {
                            res.setStatus("SUCCESS");
                        } else {
                            res.setStatus("FAIL");
                        }
                    }
                }
                auditTrailService.saveAuditTrailData("Employee", "Delete", "Admin",
                        NotificationModule.EMPLOYEE_NOMINEE_DETAILS, NotificationAction.DELETE, "/deleteNomineeDtl",
                        userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus("FAIL");
        }

        return res;
    }

    @GetMapping(value = "/getWeeklyOffAutoComplete")
    public @ResponseBody String getWeeklyOffAutoComplete(HttpServletRequest request, @RequestParam String name,
                                                         HttpSession session) {
        String json = null;
        try {

            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                return "redirect:/signin";
            }

            logger.info("name " + name);
            if (StringUtil.isNotEmpty(name)) {
                name = name + "%";
            } else {
                name = "%";
            }
            if (StringUtil.isNotEmpty(name)) {
                List<ListItems> listItems = new ArrayList<>();
                // List<WeeklyOffPolicyMaster> weeklyList =
                // weeklyOffPolicyMaster.findLikePatternName(name);
                List<WeeklyOffPolicyMaster> weeklyList = weeklyOffPolicyMaster
                        .findLikePatternNameAndCompanyAndBranch(name, companyId, companyBranchId);

                weeklyList.forEach(e -> {
                    ListItems item = new ListItems(e.getId().toString(), e.getPatternName());
                    listItems.add(item);
                });
                json = new ObjectMapper().writeValueAsString(listItems);
                logger.info(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @GetMapping(value = "/checkDuplicatePriority/{priority}/{empId}")
    public @ResponseBody Long checkDuplicatePriority(@PathVariable("priority") Long priority,
                                                     @PathVariable("empId") Long empId, Model model, HttpServletRequest request,
                                                     final RedirectAttributes redirectAttributes, HttpSession session) {
        logger.info("EmployeeController.checkDuplicatePriority() " + priority);
        Long Priority = null;
        try {

            Employee em = employeeRepository.findById(empId).orElse(null);
            Priority = nomineeRepo.checkDuplicatePriority(priority, em.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Priority;
    }

    @GetMapping(value = "/checkDuplicatePriorityEdit/{priority}/{empId}/{nomineeId}")
    public @ResponseBody Long checkDuplicatePriorityEdit(@PathVariable("nomineeId") Long nomineeId,
                                                         @PathVariable("priority") Long priority, @PathVariable("empId") Long empId, Model model,
                                                         HttpServletRequest request, final RedirectAttributes redirectAttributes, HttpSession session) {
        logger.info("EmployeeController.checkDuplicatePriority() " + priority);
        Long Priority = null;
        try {

            Employee em = employeeRepository.findById(empId).orElse(null);
            Priority = nomineeRepo.checkDuplicatePriorityEdit(priority, em.getId(), nomineeId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Priority;
    }

    @ResponseBody
    @RequestMapping("/getEmpNomineeTypeAndPercentage")
    public JsonResponse getEmpNomineeTypeAndPercentage(@RequestParam(value = "id") Long id) {

        JsonResponse jsonResponse = new JsonResponse();

        if (id != null) {

            List<Object[]> list = nomineeRepo.getEmpNomineeTypeAndPercentage(id);
            Map<String, BigInteger> map = new HashMap<String, BigInteger>();
            for (Object[] obj : list) {

                String abc = (String) obj[0];
                BigInteger def = (BigInteger) obj[1];

                System.err.println("abc " + abc);
                System.err.println("def " + def);

                map.put(abc, def);

            }
            jsonResponse.setNomineeTypePercMap(map);
            System.err.println("Map " + map);
        }

        // jsonResponse.setObj(holidayList);
        return jsonResponse;

    }

    @RequestMapping(value = "/checkIsPANExist", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> checkIsPANExist(@RequestParam("panNo") String panNo,
                                                             @RequestParam("empId") String empId, Model model) {
        logger.info("Employee >> checkIsPANExist().");
        Map<String, Object> response = new HashMap<>();

        logger.info("empId---" + empId);
        logger.info("panNo---" + panNo);

        if (StringUtil.isNotEmpty(panNo)) {
            try {
                Long result = null;
                if (StringUtil.isNotEmpty(empId))
                    result = employeeRepository.checkPANExistWithEmpId(panNo, Long.parseLong(empId));
                else
                    result = employeeRepository.checkPANExist(panNo);
                if (result != null && result == 0) {
                    response.put("Success", "Success !");
                    response.put("isUsed", false);
                }
            } catch (Exception e) {
                logger.warn("Error in checkIsPANExist()");
                e.printStackTrace();
                response.put("failure", "failure");
            }
        } else {
            logger.warn("Retry with proper code.");
            response.put("failure", "failure");
        }
        return response;
    }

    @GetMapping(value = "/getBillPayCategories")
    @ResponseBody
    public List<PayEmpGroupMst> getBillPayCategories(@RequestParam(value = "employeeType") String employeeType,
                                                     @RequestParam(value = "employeeEligibleFor") String employeeEligibleFor, HttpSession session,
                                                     HttpServletRequest request, HttpServletResponse response) {
        try {
            logger.info("EmployeeController.getBillPayCategories");

            System.err.println("==>" + employeeType);
            System.err.println("==>" + employeeEligibleFor);

            if (employeeType.equals("Contract Based or Hnorariun")) {
                List<PayEmpGroupMst> lists = payEmpGroupMstRepo.findAllByGroupName("Contract Base");
                return lists;
            }
            if (employeeType.equals("Daily Wages")) {
                List<PayEmpGroupMst> lists = payEmpGroupMstRepo.findAllByGroupName("Daily Wages");
                return lists;
            }

            if (employeeType.equals("Temporary") || employeeType.equals("Permanent")) {

                if (employeeEligibleFor.equals("NPS")) {
                    List<PayEmpGroupMst> lists = payEmpGroupMstRepo.findAllByGroupName("NPS");
                    return lists;
                }
                if (employeeEligibleFor.equals("GPS")) {
                    List<PayEmpGroupMst> lists = payEmpGroupMstRepo.findTop2();
                    return lists;
                }
                if (employeeEligibleFor.equals("Other (Contract/Dailywages)")) {
                    return new ArrayList<>();
                }

            }

            return new ArrayList<>();

        } catch (Exception e) {
            return new ArrayList<>();

        }
    }

    @GetMapping(value = "/getPayStructureBasedOnPayBillCategories")
    @ResponseBody
    public List<PayStructTemplateMaster> getPayStructureBasedOnPayBillCategories(
            // @RequestParam(value = "payBillCatgry") String payBillCatgry,
            HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        try {
            logger.info("EmployeeController.getPayStructureBasedOnPayBillCategories");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");
            // System.err.println("==>" + payBillCatgry);

            // if (payBillCatgry.equals("NPS")) {

            List<PayStructTemplateMaster> lists = paystructTemplateMstRepo
                    .findAllByCompanyIdAndCompanyBranchId(companyId, companyBranchId);

            return lists;
//			}
//			if (payBillCatgry.equals("GPS")) {
//				List<PayStructTemplateMaster> lists = paystructTemplateMstRepo
//						.findAllByCompanyIdAndCompanyBranchIdAndIsForGpsTrue(companyId, companyBranchId);
//				return lists;
//			}
            // return new ArrayList<>();

        } catch (Exception e) {
            return new ArrayList<>();

        }
    }

    @GetMapping(value = "/gettingGridBasedOnPayStructure")
    @ResponseBody
    public List<?> gettingGridBasedOnPayStructure(@RequestParam(value = "payStructId") Long payStructId,
                                                  // @RequestParam(value = "PB") String PB, @RequestParam(value = "GP") String GP,
                                                  @RequestParam(value = "DA") String DA, @RequestParam(value = "utegis") String utegis,
                                                  @RequestParam(value = "BASIC") String BASIC, @RequestParam(value = "empId") String empId,
                                                  HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        // try {
        logger.info("EmployeeController.gettingGridBasedOnPayStructure");

        System.err.println("==>" + payStructId);
        // System.err.println("PB==>" + PB);
        // System.err.println("GP==>" + GP);
        System.err.println("DA==>" + DA);
        System.err.println("utegis==>" + utegis);
        System.err.println("EmpId==>" + empId);

        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        Map<String, String> savedFormulaMap = new LinkedHashMap<String, String>();

        Map<String, String> gettingfromDynamicMethod = new LinkedHashMap<String, String>();

        List<PayStructTemplateDetail> list2 = payStructDetailRepository
                .findAllByPayStructureMasterisDelete(payStructId);

        List<String> listofelementShortNamePresentInTemplate = new ArrayList<>();

        for (PayStructTemplateDetail tempDtl : list2) {
            listofelementShortNamePresentInTemplate.add(tempDtl.getPayElement().getShortNameOfElement());
        }

        for (PayStructTemplateDetail tempDtl : list2) {
            System.err.println("Shortname ==>" + tempDtl.getPayElement().getShortNameOfElement());
//			if (tempDtl.getPayElement().getShortNameOfElement().equals("PB")) {
//				savedFormulaMap.put("PB", PB);
//			} else if (tempDtl.getPayElement().getShortNameOfElement().equals("GP")) {
//				savedFormulaMap.put("GP", GP);
//			}else
            if (tempDtl.getPayElement().getShortNameOfElement().equals("BASIC")) {
                savedFormulaMap.put("BASIC", BASIC);
            } else if (tempDtl.getPayElement().getShortNameOfElement().equals("DA")) {
                System.err.println(listofelementShortNamePresentInTemplate.toString());

                gettingfromDynamicMethod = createFormulaForSystemDefineElementInGrid(
                        listofelementShortNamePresentInTemplate, "DA", DA, companyId, companyBranchId);

                gettingfromDynamicMethod.forEach((k, v) -> {
                    System.err.println("Key: " + k + ", Value: " + v);
                    savedFormulaMap.put(k, v);
                });
            } else if (tempDtl.getPayElement().getShortNameOfElement().equals("DP")) {
                gettingfromDynamicMethod = createFormulaForSystemDefineElementInGrid(
                        listofelementShortNamePresentInTemplate, "DP", DA, companyId, companyBranchId);

                gettingfromDynamicMethod.forEach((k, v) -> {
                    System.err.println("Key: " + k + ", Value: " + v);
                    savedFormulaMap.put(k, v);
                });
            }

            // Code For UTGIS Elements
            else if (tempDtl.getPayElement().getShortNameOfElement().equals("UTEGIS_IF")
                    || tempDtl.getPayElement().getShortNameOfElement().equals("UTEGIS_SF")) {
                // find master id from which was selected from from data.
                PayUTEGISMaster utgis = payutegismasterRepo.findById(Long.parseLong(utegis)).orElse(null);

                // get employee group
                Employee employee = employeeRepository.findById(Long.parseLong(empId)).orElse(null);
                GroupMaster group = employee.getGroup();

                // find record from utegis details class with group id and utegis master Id
                UTEGISMasterDtl utegisDtl = utegisDtlRepository.findByUtegisMasterAndGroup(utgis, group);

                if (tempDtl.getPayElement().getShortNameOfElement().equals("UTEGIS_IF")) {
                    savedFormulaMap.put("UTEGIS_IF", String.valueOf(utegisDtl.getInsuranceContribution()));
                } else {
                    savedFormulaMap.put("UTEGIS_SF", String.valueOf(utegisDtl.getSavingContribution()));

                }

            }

            else {
                if (tempDtl.getFormula() != null) {
                    savedFormulaMap.put(tempDtl.getPayElement().getShortNameOfElement(),
                            tempDtl.getFormula().getFormulaForCalculation());

                }
            }
        }

        EmpApproachMst empApproachMst = empApproachMstRepository
                .findTopByEmployeeIdOrderByIdDesc(Long.parseLong(empId), companyId, companyBranchId).orElse(null);
        if (empApproachMst != null) {
            List<EmpApproachDtl> empApproachDtl = empApproachDtlRepository
                    .findByEmpApproachMstId(empApproachMst.getId());
            if (empApproachDtl.size() > 0) {
                for (EmpApproachDtl ead : empApproachDtl)
                    if (ead.getFormula() != null) {
                        savedFormulaMap.put(ead.getElement().getShortNameOfElement(), ead.getFormula());

                    }
            }

        }

        System.out.println("savedFormulaMap " + savedFormulaMap.size());
        savedFormulaMap.forEach((k, v) -> {
            System.err.println("Key: " + k + ", Value: " + v);
        });
        Map<String, Double> finalMap = new LinkedHashMap<>();
        try {
            finalMap = testNames.getValuesForDynamicCreatedExcel(savedFormulaMap, testNames);
            System.err.println(finalMap);
            finalMap.forEach((k, v) -> {
                System.err.println("Key: " + k + ", Value: " + v);
            });

        } catch (IOException e) {

            e.printStackTrace();
        }
        List<PayStructTemplateDetail> list = payStructDetailRepository.findAllByPayStructureMasterisDelete(payStructId);

        List<EmpSalaryGridDto> finalDtoList = new ArrayList<>();

        for (PayStructTemplateDetail payStruct : list) {
            EmpSalaryGridDto dto = new EmpSalaryGridDto();
            dto.setNameOfElement(payStruct.getPayElement().getNameOfElement());
            dto.setElementNature(payStruct.getPayElement().getPayElementNature());
            dto.setElementType(payStruct.getPayElement().getElementType());
            dto.setElementShortName(payStruct.getPayElement().getShortNameOfElement());
            if (null != payStruct.getFormula()) {
                dto.setFormulaName(payStruct.getFormula().getNameOfFormula());
            } else {
                dto.setFormulaName("-");
            }

            dto.setAmount(finalMap.get(payStruct.getPayElement().getShortNameOfElement()));
            dto.setApprovedAmount(finalMap.get(payStruct.getPayElement().getShortNameOfElement()));
            // by default flag null
            dto.setFlag("N");
            dto.setCafeteriaElement("N");

            if (payStruct.getRoundMaster() != null) {
                dto.setRoundType(payStruct.getRoundMaster().getRoundType());
            } else {
                dto.setRoundType("No Rounding");
            }

            dto.setAddInGross(payStruct.getPayElement().isGross());

            dto.setElementId(payStruct.getPayElement().getId());
            if (payStruct.getFormula() != null) {
                dto.setFormulaId(payStruct.getFormula().getFormulaId());
            }
            if (payStruct.getRoundMaster() != null) {
                dto.setRoundMasterId(payStruct.getRoundMaster().getId());
            }
            finalDtoList.add(dto);
        }

        if (empApproachMst != null) {
            List<EmpApproachDtl> empApproachDtl2 = empApproachDtlRepository
                    .findByEmpApproachMstId(empApproachMst.getId());
            if (empApproachDtl2.size() > 0) {
                for (EmpApproachDtl ead2 : empApproachDtl2) {
                    EmpSalaryGridDto dto = new EmpSalaryGridDto();
                    dto.setNameOfElement(ead2.getElement().getNameOfElement());
                    dto.setElementNature(ead2.getElement().getPayElementNature());
                    dto.setElementType(ead2.getElement().getElementType());
                    dto.setElementShortName(ead2.getElement().getShortNameOfElement());
                    dto.setCafeteriaElement("Y");
                    dto.setFormulaId(ead2.getId());
                    if (null != ead2.getFormula()) {
                        dto.setFormulaName(ead2.getFormula());
                    } else {
                        dto.setFormulaName("-");
                    }

                    dto.setAmount(finalMap.get(ead2.getElement().getShortNameOfElement()));
                    dto.setApprovedAmount(finalMap.get(ead2.getElement().getShortNameOfElement()));
                    // by default flag null
                    dto.setFlag("N");
                    if (ead2.getRoundMaster() != null) {
                        dto.setRoundMasterId(ead2.getRoundMaster().getId());
                        dto.setRoundType(ead2.getRoundMaster().getRoundType());
                    } else {
                        dto.setRoundType("No Rounding");
                    }

                    dto.setAddInGross(ead2.getElement().isGross());

                    dto.setElementId(ead2.getElement().getId());

                    if (ead2.getRoundMaster() != null) {
                        dto.setRoundMasterId(ead2.getRoundMaster().getId());
                    }
                    finalDtoList.add(dto);
                }
            }
        }

        /* System.err.println(finalDtoList); */

        return finalDtoList;

//		} catch (Exception e) {
//			return new ArrayList<>();
//
//		}
    }

    @GetMapping(value = "/addNewElementInStructure")
    @ResponseBody
    public EmpSalaryGridDto addNewElementInStructure(@RequestParam(value = "payelement") Long payelement,
                                                     @RequestParam(value = "formula", defaultValue = "0") Long formula,
                                                     @RequestParam(value = "roundMaster") Long roundMaster,
                                                     @RequestParam(value = "elementAmount", defaultValue = "0") String elementAmount,
                                                     @RequestHeader Map<String, String> headers, HttpSession session, HttpServletRequest request,
                                                     HttpServletResponse response) {

        try {
            logger.info("EmployeeController:addNewElementInStructure");

            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            System.err.println("payElement==>" + payelement);
            System.err.println("formula==>" + formula);
            System.err.println("roundMaster==>" + roundMaster);

            String customMap = headers.get("custom-header");

            // Convert String To Map Object

            customMap = customMap.substring(1, customMap.length() - 1);
            String[] keyValuePairs = customMap.split(",");
            Map<String, String> map = new LinkedHashMap<>();

            for (String pair : keyValuePairs) {
                String[] entry = pair.split(":");
                map.put(entry[0].trim(), (entry[1].trim()));

            }

            System.err.println(map);
            Set<String> keys = map.keySet();

            Map<String, String> map2 = new LinkedHashMap<>();

            for (String key : keys) {

                String keyAttr = key.substring(1, key.length() - 1);
                String valueAttr = map.get(key).substring(1, map.get(key).length() - 1);
                map2.put(keyAttr, valueAttr);
            }

            /* getting data for new elements */

            ElementOfPaySystemMasterEntity elementId = elementOfPaySys.findById(payelement).orElse(null);
            if (formula != 0) {
                FormulaCalculationBaseEntity formulaId = formulaCreationRepository.findById(formula).orElse(null);
                map2.put(elementId.getShortNameOfElement(), formulaId.getFormulaForCalculation());
            }
            if (!elementAmount.equals("0")) {
                map2.put(elementId.getShortNameOfElement(), elementAmount);

            }
            RoundMaster roundMasterId = roundMasterRepo.findById(roundMaster).orElse(null);

            Map<String, Double> finalMap = new LinkedHashMap<>();

            try {
                finalMap = testNames.getValuesForDynamicCreatedExcel(map2, testNames);
                System.err.println(finalMap);
                finalMap.forEach((k, v) -> {
                    System.err.println("Key: " + k + ", Value: " + v);
                });

            } catch (IOException e) {

                e.printStackTrace();
            }

            EmpSalaryGridDto addNewElement = new EmpSalaryGridDto();
            addNewElement.setElementShortName(elementId.getShortNameOfElement());
            addNewElement.setAmount(finalMap.get(elementId.getShortNameOfElement()));
            addNewElement.setElementType(elementId.getElementType());
            addNewElement.setElementNature(elementId.getPayElementNature());
            addNewElement.setNameOfElement(elementId.getNameOfElement());
            if (formula != 0) {

                FormulaCalculationBaseEntity formulaId = formulaCreationRepository.findById(formula).orElse(null);
                if (formulaId != null) {
                    addNewElement.setFormulaId(formulaId.getFormulaId());
                    addNewElement.setFormulaName(formulaId.getNameOfFormula());
                }

            }

            addNewElement.setAddInGross(elementId.isGross());
            addNewElement.setRoundType(roundMasterId.getRoundType());

            addNewElement.setElementId(elementId.getId());

            if (roundMasterId != null) {
                addNewElement.setRoundMasterId(roundMasterId.getId());
            }

            addNewElement.setApprovedAmount(finalMap.get(elementId.getShortNameOfElement()));
            addNewElement.setFlag("N");
            addNewElement.setCafeteriaElement("N");

            return addNewElement;
        } catch (Exception e) {
            e.printStackTrace();
            return new EmpSalaryGridDto();
        }

    }

    @ResponseBody
    @PostMapping("/saveEmployeeSalary")
    @Transactional
    public String saveEmployeeSalary(@ModelAttribute("dto") EmpSalaryDtl dto, HttpServletRequest request, Model model,
                                     HttpServletResponse response, HttpSession session, RedirectAttributes redirectAttributes,
                                     @RequestHeader Map<String, String> headers)
            throws JSONException, IllegalAccessException, InvocationTargetException, ParseException {

        try {
            logger.info("EmployeeController:saveEmployeeSalary");

            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            if (um == null || companyId == null || companyBranchId == null) {
                // return "redirect:/signin";
            }

            System.err.println(dto);

            if (empSalaryDtlRepository.findByEmp(dto.getEmp()) != null)
                return "Already exist";

            /* for saving data in empsalaryDtl(Pay_emp_mst) table */

            if(request.getParameter("npsApplicable") != null) {
                dto.getEmp().setEmployeeEligibleFor("NPS");
                employeeRepository.save(dto.getEmp());

            }

            EmpSalaryDtl salaryDtl = new EmpSalaryDtl();
            salaryDtl.setEmp(dto.getEmp());
            String empCode = employeeRepository.findById(dto.getEmp().getId()).orElse(null).getEmpCode();
            salaryDtl.setEmpCode(empCode);

            if (payStructureMasterRepository.findTopOrderById() != null) {
                // salaryDtl.setPayStructCode(salaryDtlRepo.findTopOrderById() + 1);12
                salaryDtl.setPayStructCode(payStructureMasterRepository.findTopOrderById() + 1);
            }

            else {
                salaryDtl.setPayStructCode(1L);
            }
            salaryDtl.setTypeOfDa(dto.getTypeOfDa());
            salaryDtl.setRevisonNo(1L);
            salaryDtl.setPayMode("BT");
            salaryDtl.setGovtPsuAfterSep(dto.isGovtPsuAfterSep());
            salaryDtl.setPayCommissionIdSalary(dto.getPayCommissionIdSalary());
            salaryDtl.setPayBandId(dto.getPayBandId());
            // salaryDtl.setSalaryRangSalary(dto.getSalaryRangSalary());
            // salaryDtl.setGradePayId(dto.getGradePayId());
            salaryDtl.setGradePay(0L);
            salaryDtl.setUtegis(dto.getUtegis());
            salaryDtl.setDa(dto.getDa());
            salaryDtl.setIncrementMonth(Integer.parseInt(request.getParameter("incrementMonth")));

            if (!dto.getEffectiveDateStr().isEmpty()) {
                salaryDtl.setEffectiveDate(DateUtil.convertStringToDate(dto.getEffectiveDateStr(), "dd/MM/yyyy"));
            }
            // salaryDtl.setEffectiveDate(dto.getEffectiveDate());
            salaryDtl.setGroupCode(dto.getGroupCode());
            salaryDtl.setGroupCodeId(payEmpGroupMstRepo.findByBudgetShortCode(dto.getGroupCode()));
            salaryDtl.setPayStructMaster(dto.getPayStructMaster());
            salaryDtl.setEmpApproachMst(dto.getEmpApproachMst());
            salaryDtl.setMonthlyGrossSalary(dto.getMonthlyGrossSalary());
            salaryDtl.setAnnualGrossSalary(dto.getAnnualGrossSalary());

            if (!dto.getApprovalDateStr().isEmpty()) {
                salaryDtl.setApprovalDate(DateUtil.convertStringToDate(dto.getApprovalDateStr(), "dd/MM/yyyy"));
            }
            // salaryDtl.setApprovalDate(dto.getApprovalDate());
            salaryDtl.setApprovalRemarks(dto.getApprovalRemarks());
            salaryDtl.setApprovedBy(dto.getEmp());

            salaryDtl.setAdmissibilityOfHra(dto.getAdmissibilityOfHra());

            if (dto.getRentFreeAccommodation() != null) {
                if (dto.getRentFreeAccommodation().equals("on")) {
                    salaryDtl.setRentFreeAccommodation("1");
                } else {
                    salaryDtl.setRentFreeAccommodation("0");
                }
            } else {
                salaryDtl.setRentFreeAccommodation("0");
            }

            if (dto.isEsic()) {
                salaryDtl.setEsic(true);
                salaryDtl.setEsicNo(dto.getEsicNo());
            } else {
                salaryDtl.setEsic(false);
            }

            if (dto.isGis()) {
                salaryDtl.setGis(true);
                salaryDtl.setGisAmount(dto.getGisAmount());
            } else {
                salaryDtl.setGis(false);
            }

            if (dto.isPf()) {
                salaryDtl.setPf(true);
                salaryDtl.setPfPercentage("PF %" + dto.getPfPercentage());
                if (!dto.getPfJoinDateStr().isEmpty()) {
                    salaryDtl.setPfJoinDate(DateUtil.convertStringToDate(dto.getPfJoinDateStr(), "dd/MM/yyyy"));
                }
            } else {
                salaryDtl.setPf(false);
            }

            if (dto.isGratuity()) {
                salaryDtl.setGratuity(true);
            } else {
                salaryDtl.setGratuity(false);
            }

            if (dto.isVpf()) {
                salaryDtl.setVpf(true);
            } else {
                salaryDtl.setVpf(false);
            }

            if (dto.isAdditionalNps()) {
                salaryDtl.setAdditionalNps(true);
            } else {
                salaryDtl.setAdditionalNps(false);
            }

            if (dto.isLwf()) {
                salaryDtl.setLwf(true);
            } else {
                salaryDtl.setLwf(false);
            }

            if (dto.isBonus()) {
                salaryDtl.setBonus(true);
            } else {
                salaryDtl.setBonus(false);
            }

            salaryDtl.setCompany(companyMasterRepository.findByIdAndIsDeleteFalse(companyId));
            salaryDtl.setCompanyBranch(companyBranchMasterRepository.findByIdAndIsDeleteFalse(companyBranchId));

            salaryDtl.setAppId(1L);
            EmpSalaryDtl salaryDtlSave = salaryDtlRepo.save(salaryDtl);

            /* for saving data in pay Struct Master table */
            PayStructMaster payStructMaster = new PayStructMaster();
            payStructMaster.setEmployee(dto.getEmp());
            payStructMaster.setEmpCode(empCode);
            System.err.println(payStructureMasterRepository.findAll().size());
            if (payStructureMasterRepository.findAll().size() != 0) {
                payStructMaster.setPayStructCode(payStructureMasterRepository.findTopOrderById() + 1);
            } else {
                payStructMaster.setPayStructCode(1L);
            }
            payStructMaster.setRevisonNo(1L);
            payStructMaster.setRevisonDate(dto.getEffectiveDate());

            if (!dto.getEffectiveDateStr().isEmpty()) {
                payStructMaster.setEffectiveDate(DateUtil.convertStringToDate(dto.getEffectiveDateStr(), "dd/MM/yyyy"));
            }
            // payStructMaster.setEffectiveDate(dto.getEffectiveDate());
            payStructMaster.setPayStructMaster(dto.getPayStructMaster());
            payStructMaster.setEmpApproachMst(dto.getEmpApproachMst());
            payStructMaster.setDa(dto.getDa());

            if (!dto.getApprovalDateStr().isEmpty()) {
                payStructMaster.setApprovalDate(DateUtil.convertStringToDate(dto.getApprovalDateStr(), "dd/MM/yyyy"));
            }
            // payStructMaster.setApprovalDate(dto.getApprovalDate());
            payStructMaster.setApprovalRemarks(dto.getApprovalRemarks());
            payStructMaster.setApprovedBy(dto.getEmp());
            payStructMaster.setPayCommission(dto.getPayCommissionIdSalary());
            payStructMaster.setPayBand(dto.getPayBandId());
            payStructMaster.setPayBandAmount(dto.getSalaryRangSalary());
            payStructMaster.setGradePayId(dto.getGradePayId());

            payStructMaster.setGroupCode(dto.getGroupCode());
            payStructMaster.setGroupCodeId(payEmpGroupMstRepo.findByBudgetShortCode(dto.getGroupCode()));

            if (dto.isEsic()) {
                payStructMaster.setEsic(true);
                payStructMaster.setEsicNo(dto.getEsicNo());
            } else {
                payStructMaster.setEsic(false);
            }

            if (dto.isGis()) {
                salaryDtl.setGis(true);
                salaryDtl.setGisAmount(dto.getGisAmount());
            } else {
                salaryDtl.setGis(false);
            }

            if (dto.isPf()) {
                payStructMaster.setPf(true);
                payStructMaster.setPfPercentage("PF %" + dto.getPfPercentage());

                if (!dto.getPfJoinDateStr().isEmpty()) {
                    payStructMaster.setPfJoinDate(DateUtil.convertStringToDate(dto.getPfJoinDateStr(), "dd/MM/yyyy"));
                }

            } else {
                payStructMaster.setPf(false);
            }

            if (dto.isGratuity()) {
                payStructMaster.setGratuity(true);
            } else {
                payStructMaster.setGratuity(false);
            }

            if (dto.isLwf()) {
                payStructMaster.setLwf(true);
            } else {
                payStructMaster.setLwf(false);
            }

            if (dto.isBonus()) {
                payStructMaster.setBonus(true);
            } else {
                payStructMaster.setBonus(false);
            }

            payStructMaster.setCompany(companyMasterRepository.findByIdAndIsDeleteFalse(companyId));
            payStructMaster.setCompanyBranch(companyBranchMasterRepository.findByIdAndIsDeleteFalse(companyBranchId));

            payStructMaster.setAppId(1L);

            payStructureMasterRepository.save(payStructMaster);

            /* for saving data in pay Struct details table */

            String customMap = headers.get("custom-header");

            // Convert String To Map Object

            customMap = customMap.substring(1, customMap.length() - 1);
            String[] keyValuePairs = customMap.split(",");
            Map<String, String> map = new LinkedHashMap<>();

            for (String pair : keyValuePairs) {
                String[] entry = pair.split(":");
                map.put(entry[0].trim(), (entry[1].trim()));

            }

            Set<String> keys = map.keySet();

            List<PayStructDetail> payStructDetails = new ArrayList<>();

            Long count = 1L;
            for (String key : keys) {

                String keyAttr = key.substring(1, key.length() - 1);
                String valueAttr = map.get(key).substring(1, map.get(key).length() - 1);

                String[] sds = valueAttr.split("-");

                String elementAmount = sds[0];
                String formulaId = sds[1];
                String roundId = sds[2];
                String manualFlag = sds[3];
                String cafeteriaFlag = sds[4];

                PayStructDetail payStructDetail = new PayStructDetail();
                payStructDetail.setPayStructCode(payStructMaster.getPayStructCode());
                payStructDetail.setRevisonNo(1L);
                payStructDetail.setElemSeq(count);
                payStructDetail.setElemOrder(count);

                ElementOfPaySystemMasterEntity element = elementOfPaySys.findTop1ByShortNameOfElement(keyAttr);
                payStructDetail.setPeElementType(element.getElementType());
                payStructDetail.setPayElement(element);
                payStructDetail.setPayElementAmount(Double.parseDouble(elementAmount));
                payStructDetail.setPayElementCurrCd("INR");
                payStructDetail.setPayStructureMaster(payStructMaster);

                if (element.getId() == 57) {
                    salaryDtlSave.setSalaryRangSalary(Double.parseDouble(elementAmount));
                    salaryDtlRepo.save(salaryDtlSave);
                }

//				PayStructTemplateDetail payStructTemplateDetail = payStructDetailRepository.findByPayStructureMasterAndPayElement(dto.getPayStructMaster(),element);
//
//				if(payStructTemplateDetail != null)
//				{
//				if(payStructTemplateDetail.getFormula() != null)
//				{
//				payStructDetail.setCalacCtrlNo(payStructTemplateDetail.getFormula().getFormulaId());
//				payStructDetail.setFormula(payStructTemplateDetail.getFormula());
//				}
//				else {
//					payStructDetail.setCalacCtrlNo(null);
//					payStructDetail.setFormula(null);
//					}
//				if(payStructTemplateDetail.getRoundMaster() != null)
//				{
//				payStructDetail.setRoundMaster(payStructTemplateDetail.getRoundMaster());
//				}
//				else {
//					payStructDetail.setRoundMaster(null);
//				}
//				}
//				payStructDetail.setCompany(companyMasterRepository.findByIdAndIsDeleteFalse(companyId));
//				payStructDetail.setCompanyBranch(companyBranchMasterRepository.findByIdAndIsDeleteFalse(companyBranchId));

                System.err.println("=======>" + formulaId.length());
                /* Here checking for n means formula is not null */
                if (cafeteriaFlag.equals("Y")) {
                    payStructDetail.setCalacCtrlNo(null);
                    payStructDetail.setFormula(null);
                } else if (!formulaId.startsWith("n")) {

                    payStructDetail.setCalacCtrlNo(Long.parseLong(formulaId));
                    payStructDetail
                            .setFormula(formulaCreationRepository.findById(Long.parseLong(formulaId)).orElse(null));
                } else {

                    payStructDetail.setCalacCtrlNo(null);
                    payStructDetail.setFormula(null);

                }
                if (!roundId.startsWith("n")) {
                    payStructDetail.setRoundMaster(roundMasterRepo.findById(Long.parseLong(roundId)).orElse(null));

                } else {
                    payStructDetail.setRoundMaster(null);
                }
                if (manualFlag.equalsIgnoreCase("Y")) {
                    payStructDetail.setManualEntry(true);
                } else {
                    payStructDetail.setManualEntry(false);
                }

                payStructDetail.setAppId(1L);

                payStructDetail.setCompany(companyMasterRepository.findByIdAndIsDeleteFalse(companyId));
                payStructDetail
                        .setCompanyBranch(companyBranchMasterRepository.findByIdAndIsDeleteFalse(companyBranchId));

                payStructDetails.add(payStructDetail);

                count++;
            }

            payStructureDetailsRepository.saveAll(payStructDetails);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";

        }

    }

    @GetMapping(value = "/addApplicables")
    @ResponseBody
    public EmpSalaryGridDto addApplicables(@RequestParam(value = "applicableName") String applicableName,
                                           @RequestHeader Map<String, String> headers, HttpSession session, HttpServletRequest request,
                                           HttpServletResponse response) {

        /* try { */

        logger.info("EmployeeController:addNewElementInStructure");

        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        System.err.println("applicableName==>" + applicableName);

        String customMap = headers.get("custom-header");

        // Convert String To Map Object

        customMap = customMap.substring(1, customMap.length() - 1);
        String[] keyValuePairs = customMap.split(",");
        Map<String, String> map = new LinkedHashMap<>();

        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            if (entry.length >= 2) {
                map.put(entry[0].trim(), (entry[1].trim()));
            }
        }
        Set<String> keys = map.keySet();
        Map<String, String> map2 = new LinkedHashMap<>();
        List<String> elemementShortName = new ArrayList<>();

        for (String key : keys) {

            String keyAttr = key.substring(1, key.length() - 1);
            String valueAttr = map.get(key).substring(1, map.get(key).length() - 1);
            map2.put(keyAttr, valueAttr);
            elemementShortName.add(key);
        }

        /*
         * store element shortname and pass to method for checking element is applicable
         * for count or not
         */
        Map<String, String> applicableFormula = createFormulaForApplicable(elemementShortName, applicableName,
                companyId, companyBranchId);

        System.err.println("1234434==>" + applicableFormula);
        if (!applicableFormula.values().isEmpty()) {
            /* After getting result need to figureout key and value */
            Set<String> applicableFormulakeys = applicableFormula.keySet();

            /*
             * Need to pass this element into main map for excel calculation with key like
             * applicable type(ESIC) and value like `BASIC + DA * 12 %`
             */
            for (String key : applicableFormulakeys) {

                // String keyAttr = key.substring(1, key.length() - 1);
                // String valueAttr = map.get(key).substring(1, map.get(key).length() - 1);

                String keyAttr = key;
                String valueAttr = applicableFormula.get(key);

                System.err.println("key======>" + key);
                System.err.println("value======>" + valueAttr);
                map2.put(keyAttr, valueAttr);
            }

            Map<String, Double> finalMap = new LinkedHashMap<>();
            /* Need to pass whole map into excel calculation */
            try {
                finalMap = testNames.getValuesForDynamicCreatedExcel(map2, testNames);
                System.err.println(finalMap);
                finalMap.forEach((k, v) -> {
                    System.err.println("Key: " + k + ", Value: " + v);
                });

            } catch (IOException e) {

                e.printStackTrace();
            }

            /*
             * After getting successful result from excel calculation setting value in dto
             * for add into grid
             */
            ElementOfPaySystemMasterEntity elementOfPaySystemMasterEntity = elementOfPaySys
                    .findTop1ByShortNameOfElement(applicableName);

            EmpSalaryGridDto addNewElement = new EmpSalaryGridDto();
            addNewElement.setElementShortName(elementOfPaySystemMasterEntity.getShortNameOfElement());

            Double amount = finalMap.get(elementOfPaySystemMasterEntity.getShortNameOfElement());
            if (applicableName.equalsIgnoreCase("ESIC")) {
                ESIC esicEntity = esicRepository.findByLastRecord(companyId, companyBranchId);

                if (amount > esicEntity.getMaxLimit()) {
                    addNewElement.setAmount(esicEntity.getMaxLimit());
                    addNewElement.setApprovedAmount(esicEntity.getMaxLimit());
                } else {
                    addNewElement.setAmount(amount);
                    addNewElement.setApprovedAmount(amount);
                }

            } else if (applicableName.equalsIgnoreCase("PF")) {

                ProvidentFund pfEntity = providentFundRepository.findByLastRecord(companyId, companyBranchId);

//				if (amount > pfEntity.getEmployeesMaxLimit()) {
//					addNewElement.setAmount(pfEntity.getEmployeesMaxLimit());
//					addNewElement.setApprovedAmount(pfEntity.getEmployeesMaxLimit());
//				} else {
                addNewElement.setAmount(amount);
                addNewElement.setApprovedAmount(amount);
//				}

            } else if (applicableName.equalsIgnoreCase("NPS")) {
                addNewElement.setAmount(amount);
                addNewElement.setApprovedAmount(amount);

            } else if (applicableName.equalsIgnoreCase("Gratuity")) {

                addNewElement.setAmount(0.0);
                addNewElement.setApprovedAmount(0.0);

            } else if (applicableName.equalsIgnoreCase("LWF")) {

                addNewElement.setAmount(0.0);
                addNewElement.setApprovedAmount(0.0);

            } else if (applicableName.equalsIgnoreCase("VPF")) {

                addNewElement.setAmount(0.0);
                addNewElement.setApprovedAmount(0.0);

            } else if (applicableName.equalsIgnoreCase("NPSE")) {

                addNewElement.setAmount(0.0);
                addNewElement.setApprovedAmount(0.0);

            }

            // Parth change

            else if (applicableName.equalsIgnoreCase("GIS")) {

                addNewElement.setAmount(amount);
                addNewElement.setApprovedAmount(amount);

            }

//			else if(applicableName.equalsIgnoreCase("Bonus")) {
//
//			}
            // addNewElement.setAmount(finalMap.get(elementOfPaySystemMasterEntity.getShortNameOfElement()));
            addNewElement.setElementType(elementOfPaySystemMasterEntity.getElementType());
            addNewElement.setElementNature(elementOfPaySystemMasterEntity.getPayElementNature());
            addNewElement.setNameOfElement(elementOfPaySystemMasterEntity.getNameOfElement());
            if (applicableName.equalsIgnoreCase("Gratuity")) {
                addNewElement.setFormulaName("-");
            } else if (applicableName.equalsIgnoreCase("LWF")) {
                addNewElement.setFormulaName("-");
            } else if (applicableName.equalsIgnoreCase("VPF")) {
                addNewElement.setFormulaName("-");
            } else {
                addNewElement.setFormulaName(applicableFormula.get(applicableName));
            }

            addNewElement.setAddInGross(elementOfPaySystemMasterEntity.isGross());
            addNewElement.setRoundType("Round to two digits");

            addNewElement.setElementId(elementOfPaySystemMasterEntity.getId());
            addNewElement.setRoundMasterId(2L);

            addNewElement.setFlag("N");
            addNewElement.setCafeteriaElement("N");

            return addNewElement;
        } else {

            return new EmpSalaryGridDto();
        }

    }

    @ResponseBody
    @GetMapping(value = "/getIfEmployeeEligibleForNPS/{id}")
    public JsonResponse getIfEmployeeEligibleForNPS(@PathVariable("id") Long id, HttpSession session, HttpServletRequest request,
                                                    HttpServletResponse response) {

        logger.info("EmployeeController:addNewElementInStructure");
        JsonResponse res = new JsonResponse();
        try {
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");


            EmpPersonalInfo employee = empPersonalInfoRepository.findByEmpId(id);

            Date dob = employee.getDateOfBirth();

            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dob);

            // Get year, month, and day from Calendar
            int yearOfBirth = dobCalendar.get(Calendar.YEAR);
            int monthOfBirth = dobCalendar.get(Calendar.MONTH) + 1; // Month starts from 0
            int dayOfBirth = dobCalendar.get(Calendar.DAY_OF_MONTH);

            LocalDate date = LocalDate.of(yearOfBirth, monthOfBirth, dayOfBirth);
            LocalDate todate = LocalDate.now();

            Period period = Period.between(date, todate);
            int age = period.getYears();
            System.err.println("date Age : "+date);
            System.err.println("todate Age : "+todate);
            System.err.println("Employee Age : "+age);
            NPSConfiguration npsConfiguration =  npsConfigurationRepository.findByNPSLattestRecordWithYear(companyId, companyBranchId);

            if(npsConfiguration != null) {
                if(npsConfiguration.getMaxAgeLimit() > age) {
                    res.setStatus("SUCCESS");
                    return res;
                }else {
                    res.setStatus("NOTELIGIBLE");
                    return res;
                }

            }else {
                res.setStatus("NOTFOUNDNPSCONFOG");
                return res;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }
        return res;

    }

    public Map<String, String> createFormulaForApplicable(List<String> elementShortNames, String applicableName,
                                                          long comanyId, long companyBrachId) {

        String formula = "";
        String finalFormula = "";
        Map<String, String> applicableAndFormula = new LinkedHashMap<>();
        /*
         * Need to add loop here for every elements present in structure and create a
         * formula based on applicable type
         */
        for (String elementShortName : elementShortNames) {
            System.err.println(elementShortName);
            /*
             * Here short is in string but pass like "GB" so need to remove first and last
             * double colaun
             */
            ElementOfPaySystemMasterEntity elementOfPaySystemMasterEntity = elementOfPaySys
                    .findTop1ByShortNameOfElement(elementShortName.substring(1, elementShortName.length() - 1));

            /* Applicable type wise conditions */
//			if (applicableName.equals("Gratuity")) {
//
//				formula += "0.0";
//
//
//			}
            if (applicableName.equals("PF")) {
                /*
                 * Also check here only those elements are add where applicable type is true in
                 * element table
                 */
                if (elementOfPaySystemMasterEntity.isPf()) {
                    formula += elementOfPaySystemMasterEntity.getShortNameOfElement() + "+";

                }

            } else if (applicableName.equals("ESIC")) {

                if (elementOfPaySystemMasterEntity.isEsic()) {

                    formula += elementOfPaySystemMasterEntity.getShortNameOfElement() + "+";

                }

            } else if (applicableName.equals("NPS")) {

                if (elementOfPaySystemMasterEntity.isNps()) {

                    formula += elementOfPaySystemMasterEntity.getShortNameOfElement() + "+";

                }

            }
            /*
             * else if(applicableName == "bonus") { if(elementOfPaySystemMasterEntity.isg) {
             *
             * }
             *
             * }
             */

        }

        /*
         * Here scenirao is create formula like `BASIC + DA +` so need to remove last
         * plus and store this string value in different string
         */

        System.err.println("====>" + formula);
        if (!formula.isEmpty()) {
            finalFormula += formula.substring(0, formula.length() - 1);

            System.err.println(applicableName);

            System.err.println(finalFormula);
            /* Here append string with * and % for calculating formula in excel purpose */

            String percentage = "";

            if (applicableName.equalsIgnoreCase("ESIC")) {

                percentage += String.valueOf(esicRepository.findByLastRecord(comanyId, companyBrachId).getEEContr());

            } else if (applicableName.equalsIgnoreCase("PF")) {

                percentage += String.valueOf(
                        providentFundRepository.findByLastRecord(comanyId, companyBrachId).getEmployeesContribution());

            } else if (applicableName.equalsIgnoreCase("NPS")) {
                LocalDate todate = LocalDate.now();
                percentage += String.valueOf(
                        npsConfigurationRepository.findByNPSLattestRecordWithYear( comanyId, companyBrachId).getEmpContrib());

            }
//		else if(applicableName.equalsIgnoreCase("Gratuity")) {
//
//
//		}
//		else if(applicableName.equalsIgnoreCase("Bonus")) {
//
//			//bonusRepository.findByIdOrderByDesc().get;
//		}

            applicableAndFormula.put(applicableName, "( " + finalFormula + " ) *" + percentage + "%");

            System.err.println(applicableAndFormula);
            return applicableAndFormula;
        } else {

            if (applicableName.equals("Gratuity")) {

                formula += "0.0";

                applicableAndFormula.put(applicableName, formula);

                System.err.println(applicableAndFormula);
                return applicableAndFormula;
            } else if (applicableName.equals("LWF")) {
                formula += "0.0";

                applicableAndFormula.put(applicableName, formula);

                System.err.println(applicableAndFormula);
                return applicableAndFormula;
            } else if (applicableName.equals("VPF")) {
                formula += "0.0";

                applicableAndFormula.put(applicableName, formula);

                System.err.println(applicableAndFormula);
                return applicableAndFormula;
            } else if (applicableName.equals("NPSE")) {
                formula += "0.0";

                applicableAndFormula.put(applicableName, formula);

                System.err.println(applicableAndFormula);
                return applicableAndFormula;
            }


            // Parth change

            else if(applicableName.equals("GIS")){

//                formula += gisMasterRepository.findTop1ByIsDeleteFalse().getGisAmount().toString();

                // Parth has changed.

                formula += gisMasterRepository.findTop1ByIsDeleteFalseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(comanyId,companyBrachId).orElseThrow().getGisAmount().toString();

                applicableAndFormula.put(applicableName,formula);
                System.err.println(applicableAndFormula);
                return applicableAndFormula;


            }



            else {
                return new LinkedHashMap<>();
            }

        }

    }

    public Map<String, String> createFormulaForSystemDefineElementInGrid(List<String> elementShortNames,
                                                                         String elementName, String daPercentage, long comanyId, long companyBrachId) {

        String formula = "";
        String finalFormula = "";
        Map<String, String> systemDefineElementAndFormula = new LinkedHashMap<>();
        /*
         * Need to add loop here for every elements present in structure and create a
         * formula based on element
         */
        for (String elementShortName : elementShortNames) {
            System.err.println(elementShortName);

            ElementOfPaySystemMasterEntity elementOfPaySystemMasterEntity = elementOfPaySys
                    .findTop1ByShortNameOfElement(elementShortName);

            if (elementName.equals("DA")) {
                /*
                 * Also check here only those elements are add where element checkbox is true in
                 * element table
                 */
                if (elementOfPaySystemMasterEntity.isDearnessAllowance()) {
                    formula += elementOfPaySystemMasterEntity.getShortNameOfElement() + "+";

                }

            } else if (elementName.equals("DP")) {

                if (elementOfPaySystemMasterEntity.isDearnessPay()) {

                    formula += elementOfPaySystemMasterEntity.getShortNameOfElement() + "+";

                }

            }
        }
        /*
         *
         * /* Here scenirao is create formula like `BASIC + DA +` so need to remove last
         * plus and store this string value in different string
         */

        System.err.println("====>" + formula);
        if (!formula.isEmpty()) {
            finalFormula += formula.substring(0, formula.length() - 1);

            System.err.println(elementName);

            System.err.println(finalFormula);
            /* Here append string with * and % for calculating formula in excel purpose */

            String percentage = "";

            if (elementName.equalsIgnoreCase("DA")) {

                percentage += String.valueOf(daPercentage);

            } else if (elementName.equalsIgnoreCase("DP")) {

                percentage += String.valueOf(
                        dpConfigurationRepository.findByLastRecord(comanyId, companyBrachId).getRateofDearnessPay());

            }

            systemDefineElementAndFormula.put(elementName, "( " + finalFormula + " ) *" + percentage + "%");

            System.err.println(systemDefineElementAndFormula);
            return systemDefineElementAndFormula;
        }

        else {
            System.err.println("PLEASE CHECK ATLEAST ONE CHECKBOX FOR ELEMENT CALCULATION");
            return new LinkedHashMap<>();
        }

    }

    /*
     * public String formulaConverter(String str,String PB,String GP,String DA) {
     * String alpha = new String();
     *
     *
     * for (int i=0; i<str.length(); i++) { if (Character.isDigit(str.charAt(i)))
     * alpha+=(" "+str.charAt(i) +" "); else
     * if(Character.isAlphabetic(str.charAt(i))) alpha+=(str.charAt(i)); else
     * alpha+=(" "+str.charAt(i)+" "); }
     *
     *
     * String[] splitStr = alpha.split("\\s+");
     *
     * String exactWordReplaced = ""; for(String str2 : splitStr) {
     *
     * Pattern p = Pattern.compile("[a-zA-Z]"); Matcher m = p.matcher(str2);
     *
     *
     * if(m.find()) {
     *
     * ElementOfPaySystemMasterEntity element =
     * elementOfPaySys.findByShortNameOfElement(str2);
     * if(element.getShortNameOfElement().equals("GP")) { exactWordReplaced += GP; }
     * if(element.getShortNameOfElement().equals("PB")) { exactWordReplaced += PB; }
     * if(element.getShortNameOfElement().equals("DA")) { exactWordReplaced += DA; }
     *
     *
     * } else { exactWordReplaced += str2; }
     *
     * }
     *
     * return exactWordReplaced;
     *
     *
     * }
     */

    @RequestMapping("/myDatatable")
    @ResponseBody
    public DataTablesOutput<T> datatable(@Valid DataTablesInput input,
                                         @RequestParam Map<String, String> allRequestParams, HttpSession session) {

        DataTablesOutput<T> dataTablesOutput = null;

        // parameter set
        String firstName = allRequestParams.get("firstName");
        String lastName = allRequestParams.get("lastName");

        // create a query based on parameter
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT *  FROM public.test_view");

        if (firstName != null && !firstName.isEmpty()) {
            queryBuilder.append(" WHERE lower(first_name) like '%").append(firstName.toLowerCase()).append("%'");
        }

        if (lastName != null && !lastName.isEmpty()) {
            queryBuilder.append(" AND lower(last_name) like '%").append(lastName.toLowerCase()).append("%'");
        }

        // datatable search options
        if (!allRequestParams.get("search.value").toString().isEmpty()) {
            queryBuilder.append(" where lower(first_name) like '%")
                    .append(allRequestParams.get("search.value").toString().toLowerCase()).append("%')");
            queryBuilder.append(" OR lower(last_name) like '%")
                    .append(allRequestParams.get("search.value").toString().toLowerCase()).append("%')");
        }

        try {
            dataTablesOutput = processDatatable(input, allRequestParams, session, queryBuilder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataTablesOutput;

    }

    @PostMapping("/empReport")
    public void empReport(HttpServletRequest request, HttpSession session, HttpServletResponse response,
                          @RequestParam Map<String, String> allRequestParams) {

        HashMap<String, Object> jasperParameter = new HashMap<>();

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        if (firstName.isEmpty()) {
            jasperParameter.put("firstName", "0");
        } else {
            jasperParameter.put("firstName", firstName);
        }

        if (lastName.isEmpty()) {
            jasperParameter.put("lastName", "0");
        } else {
            jasperParameter.put("lastName", lastName);
        }

        try {
            processJasperReport(request, session, response, jasperParameter, JrxmlConstant.TEST_JRXML_FILE,
                    JasperExportConstant.SALARY_SLIP);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @PostMapping("/salarySlip")
    public void salarySlip(HttpServletRequest request, HttpSession session, HttpServletResponse response,
                           @RequestParam Map<String, String> allRequestParams) {

        HashMap<String, Object> jasperParameter = new HashMap<>();

        String payYr = request.getParameter("payYr");
        String payMn = request.getParameter("payMn");
        String employeeId = request.getParameter("employeeId");

        if (payYr.isEmpty()) {
            jasperParameter.put("payYr", "0");
        } else {
            jasperParameter.put("payYr", payYr);
        }

        if (payMn.isEmpty()) {
            jasperParameter.put("payMn", "0");
        } else {
            jasperParameter.put("payMn", payMn);
        }

        if (employeeId.isEmpty()) {
            jasperParameter.put("employeeId", "0");
        } else {
            jasperParameter.put("employeeId", employeeId);
        }

        jasperParameter.put("payYr", Integer.parseInt(payYr));
        jasperParameter.put("empcd", employeeId);
        jasperParameter.put("payMn", Integer.parseInt(payMn));
        jasperParameter.put("path", request.getServletContext().getRealPath("/") + "reports/");
        jasperParameter.put("imagePath", "D:/opt/mount/hrms/1/1/logos/");
        jasperParameter.put("runno", 0);

        try {
            processJasperReport(request, session, response, jasperParameter, JrxmlConstant.SALARY_SLIP,
                    JasperExportConstant.SALARY_SLIP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/salarySlipData")
    @ResponseBody
    public String salarySlipData(HttpServletRequest request, HttpSession session, HttpServletResponse response,
                                 @RequestParam Map<String, String> allRequestParams) {

        HashMap<String, Object> jasperParameter = new HashMap<>();

        String payYr = request.getParameter("payYr");
        String payMn = request.getParameter("payMn");
        String employeeId = request.getParameter("employeeId");

        if (payYr.isEmpty()) {
            jasperParameter.put("payYr", "0");
        } else {
            jasperParameter.put("payYr", payYr);
        }

        if (payMn.isEmpty()) {
            jasperParameter.put("payMn", "0");
        } else {
            jasperParameter.put("payMn", payMn);
        }

        if (employeeId.isEmpty()) {
            jasperParameter.put("employeeId", "0");
        } else {
            jasperParameter.put("employeeId", employeeId);
        }

//		jasperParameter.put("payYr", Integer.parseInt(payYr));
//		jasperParameter.put("empcd", employeeId);
//		jasperParameter.put("payMn", Integer.parseInt(payMn));
        jasperParameter.put("path", "/reports/");
        jasperParameter.put("imagePath", "D:/opt/mount/hrms/1/1/logos/");
//		jasperParameter.put("runno", 0);
        jasperParameter.put("reportTitle", JasperExportConstant.SALARY_SLIP);

        String fileName = SilverUtil.removeExtension(JrxmlConstant.SALARY_SLIP);

        try {
            processJasperReport(request, session, response, jasperParameter, JrxmlConstant.SALARY_SLIP,
                    JasperExportConstant.SALARY_SLIP);
            Path path = Paths.get(request.getServletContext().getRealPath("/") + "TempFiles"
                    + System.getProperty("file.separator") + fileName + ".pdf");
            Files.exists(path);

            if (Files.exists(path)) {
                fileName = fileName + ".pdf";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @ResponseBody
    @GetMapping(value = "/getCafeteriaConfigurationList/{id}")
    public List<CafeteriaApproachMaster> getCafeteriaConfigurationList(@PathVariable("id") Long approachId,
                                                                       HttpSession session, HttpServletRequest request, HttpServletResponse response) {

        logger.info("getCafeteriaConfigurationList()");
        List<CafeteriaApproachMaster> cafeteriaApproachMasters = null;
        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        try {
            cafeteriaApproachMasters = cafeteriaConfigurationRepository.findAllIsDeleteFalseAndApproachLevel(approachId,
                    companyId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cafeteriaApproachMasters;
    }

    @GetMapping(value = "/enableDisableAllowanceConfiguration")
    @ResponseBody
    public String enableDisableAllowanceConfiguration(@RequestParam(value = "flag") String flag,
                                                      @RequestParam(value = "empId", defaultValue = "0") String empId, HttpSession session,
                                                      HttpServletRequest request, HttpServletResponse response) {
        // try {
        logger.info("EmployeeController.enableDisableAllowanceConfiguration");

        logger.info("flag " + flag + " empId " + empId);
        try {

            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            employeeService.enableDisableAllowanceConfiguration(flag, Long.parseLong(empId));

            String message = "";
            if (flag.equalsIgnoreCase("Y")) {
                message = "Request For Allowance Declaration Enable Successfully.";
            } else if (flag.equalsIgnoreCase("N")) {
                message = "Allowance Declaration Disabled Successfully.";
            }
            notificationMasterService.sendNotification(CommonConstant.GENERAL_APPLICATION, "Allowance Declaration",
                    message, um, Arrays.asList(userMasterRepository.findByEmpId(Long.parseLong(empId))),
                    "hrms/employee/editEmployee/" + empId, false, companyId, companyBranchId);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }

    }

    @RequestMapping(value = "/getEmployeeDtl")
    public @ResponseBody JsonResponse getEmpDetails(@RequestParam("employeeId") Long employeeId, HttpSession session) {
        logger.info("employee id " + employeeId);

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            res.setStatus("FAIL");
            return res;
        }

        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee != null) {
            res.setObj(employee);
            res.setStatus("success");
        }

        return res;
    }

    @GetMapping("/getGenderByRelation")
    public @ResponseBody FamilyRelationMaster getGenderByRelation(
            @RequestParam("familyRelationId") Long familyRelationId, HttpServletRequest request, HttpSession session) {
        logger.info("getGenderByRelation called");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");
        FamilyRelationMaster familyDetails = familyRelationMasterRepository
                .findByRelationIdAndComIdAndBranchIdAndIsDeleteFalse(familyRelationId, companyId, companyBranchId);
        return familyDetails;
    }

    @PostMapping(value = "/calculateSalaryAfterGrid")
    @ResponseBody
    public List<?> calculateSalaryStructureAfterGrid(HttpSession session, HttpServletRequest request,
                                                     @RequestHeader Map<String, String> headers, @RequestParam(value = "DA") String DA,
                                                     @RequestParam(value = "utegis") String utegis, @RequestParam(value = "empId") String empId,
                                                     HttpServletResponse response) {

        UserMaster um = (UserMaster) session.getAttribute("usermaster");

        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        if (um == null || companyId == null || companyBranchId == null) {
            // return "redirect:/signin";
        }

        String customMap = headers.get("custom-header");

        // Convert String To Map Object

        customMap = customMap.substring(1, customMap.length() - 1);
        String[] keyValuePairs = customMap.split(",");
        Map<String, String> map = new LinkedHashMap<>();

        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            map.put(entry[0].trim(), (entry[1].trim()));

        }

        System.err.println(map);
        Set<String> keys = map.keySet();

        Map<String, List<String>> map2 = new LinkedHashMap<>();
        Map<String, String> gettingfromDynamicMethod = new LinkedHashMap<String, String>();

        Map<String, String> savedFormulaMap = new LinkedHashMap<String, String>();

        for (String key : keys) {

            String keyAttr = key.substring(1, key.length() - 1);
            String valueAttr = map.get(key).substring(1, map.get(key).length() - 1);

            String[] sds = valueAttr.split("@");

            String elementAmount = sds[0];
            String formulaId = sds[1];
            String roundId = sds[2];
            String approvedAmount = sds[3];
            String flag = sds[4];
            String cafeteria = sds[5];

            if (flag.equals("Y")) {
                map2.put(keyAttr, Arrays.asList(flag, approvedAmount));

            }

            else if (cafeteria.equals("Y")) {
                map2.put(keyAttr, Arrays.asList(flag, formulaId));
            }

            else {

                if (keyAttr.equals("GS") || keyAttr.equals("PB") || keyAttr.equals("GP")
                        || !formulaId.matches("\\d+")) {
                    map2.put(keyAttr, Arrays.asList(flag, approvedAmount));
                }

                else {
                    map2.put(keyAttr, Arrays.asList(flag, formulaId));
                }

            }

        }

        for (String elementShortName : map2.keySet()) {

            String valueAttr = map.get("\"" + elementShortName + "\"").substring(1,
                    map.get("\"" + elementShortName + "\"").length() - 1);

            String[] sds = valueAttr.split("@");

            String formulaId = sds[1];

            String cafeteria = sds[5];

            ElementOfPaySystemMasterEntity element = elementOfPaySystemMasterRepository
                    .findTop1ByShortNameOfElement(elementShortName);

            if (element.getShortNameOfElement().equals("DA")) {

                if (!map2.get(elementShortName).get(0).equals("Y")) {

                    gettingfromDynamicMethod = createFormulaForSystemDefineElementInGrid(new ArrayList<>(map2.keySet()),
                            "DA", DA, companyId, companyBranchId);

                    gettingfromDynamicMethod.forEach((k, v) -> {
                        System.err.println("Key: " + k + ", Value: " + v);
                        savedFormulaMap.put(k, v);
                    });
                } else {

                    savedFormulaMap.put(elementShortName, map2.get(elementShortName).get(1));

                }
            }

            else if (element.getShortNameOfElement().equals("DP")) {

                if (!map2.get(elementShortName).get(0).equals("Y")) {

                    gettingfromDynamicMethod = createFormulaForSystemDefineElementInGrid(new ArrayList<>(map2.keySet()),
                            "DP", DA, companyId, companyBranchId);

                    gettingfromDynamicMethod.forEach((k, v) -> {
                        System.err.println("Key: " + k + ", Value: " + v);
                        savedFormulaMap.put(k, v);
                    });
                } else {

                    savedFormulaMap.put(elementShortName, map2.get(elementShortName).get(1));

                }
            }

            // Code For UTGIS Elements
            else if (element.getShortNameOfElement().equals("UTEGIS_IF")
                    || element.getShortNameOfElement().equals("UTEGIS_SF")) {

                if (!map2.get(elementShortName).get(0).equals("Y")) {

                    // find master id from which was selected from from data.
                    PayUTEGISMaster utgis = payutegismasterRepo.findById(Long.parseLong(utegis)).orElse(null);

                    // get employee group
                    Employee employee = employeeRepository.findById(Long.parseLong(empId)).orElse(null);
                    GroupMaster group = employee.getGroup();

                    // find record from utegis details class with group id and utegis master Id
                    UTEGISMasterDtl utegisDtl = utegisDtlRepository.findByUtegisMasterAndGroup(utgis, group);

                    if (element.getShortNameOfElement().equals("UTEGIS_IF")) {
                        savedFormulaMap.put("UTEGIS_IF", String.valueOf(utegisDtl.getInsuranceContribution()));
                    } else {
                        savedFormulaMap.put("UTEGIS_SF", String.valueOf(utegisDtl.getSavingContribution()));

                    }
                } else {
                    savedFormulaMap.put(elementShortName, map2.get(elementShortName).get(1));

                }

            }

            else {

                if (map2.get(elementShortName).get(0).equals("Y") || elementShortName.equals("GS")
                        || elementShortName.equals("PB") || elementShortName.equals("GP")
                        || !formulaId.matches("\\d+")) {
                    savedFormulaMap.put(elementShortName, map2.get(elementShortName).get(1));
                }

                else if (cafeteria.equals("Y")) {
                    savedFormulaMap.put(elementShortName,
                            empApproachDtlRepository.findById(Long.parseLong(formulaId)).orElse(null).getFormula());
                }

                else {

                    FormulaCalculationBaseEntity formula = formulaCreationRepository
                            .findById(Long.parseLong(map2.get(elementShortName).get(1))).orElse(null);
                    if (formula != null) {
                        savedFormulaMap.put(elementShortName, formula.getFormulaForCalculation());
                    } else {
                        savedFormulaMap.put(elementShortName, "2");
                    }
                }
            }

        }

        savedFormulaMap.forEach((k, v) -> {
            System.err.println(" SavedFormulaKey: " + k + ", SavedFormulaValue: " + v);
        });

        Map<String, Double> finalMap = new LinkedHashMap<>();
        try {
            finalMap = testNames.getValuesForDynamicCreatedExcel(savedFormulaMap, testNames);
            System.err.println(finalMap);
            finalMap.forEach((k, v) -> {
                System.err.println("Key: " + k + ", Value: " + v);
            });

        } catch (IOException e) {

            e.printStackTrace();
        }

        List<EmpSalaryGridDto> finalDtoList = new ArrayList<>();

        for (String elementShortName : map2.keySet()) {
            ElementOfPaySystemMasterEntity element = elementOfPaySystemMasterRepository
                    .findTop1ByShortNameOfElement(elementShortName);

            String valueAttr = map.get("\"" + elementShortName + "\"").substring(1,
                    map.get("\"" + elementShortName + "\"").length() - 1);

            String[] sds = valueAttr.split("@");

            String elementAmount = sds[0];
            String formulaId = sds[1];
            String roundId = sds[2];
            String approvedAmount = sds[3];
            String flag = sds[4];
            String cafeteria = sds[5];

            EmpSalaryGridDto dto = new EmpSalaryGridDto();
            dto.setNameOfElement(element.getNameOfElement());
            dto.setElementNature(element.getPayElementNature());
            dto.setElementType(element.getElementType());
            dto.setElementShortName(element.getShortNameOfElement());

            if (cafeteria.equals("Y")) {
                dto.setFormulaId(Long.parseLong(formulaId));
                dto.setFormulaName(
                        empApproachDtlRepository.findById(Long.parseLong(formulaId)).orElse(null).getFormula());
            }

            else if (formulaId.matches("\\d+")) {

                FormulaCalculationBaseEntity formula = formulaCreationRepository.findById(Long.parseLong(formulaId))
                        .orElse(null);

                if (formula != null) {
                    dto.setFormulaName(formula.getNameOfFormula());
                    dto.setFormulaId(formula.getFormulaId());
                } else {
                    dto.setFormulaName("-");
                }

            }

            else {

                dto.setFormulaName("-");
            }

            dto.setCafeteriaElement(cafeteria);
            dto.setAmount(Double.parseDouble(elementAmount));
            dto.setApprovedAmount(finalMap.get(elementShortName));
            // by default flag null
            dto.setFlag(flag);

            if (roundId.matches("\\d+")) {

                RoundMaster roundMaster = roundMasterRepo.findById(Long.parseLong(roundId)).orElse(null);

                if (roundMaster != null) {
                    dto.setRoundType(roundMaster.getRoundType());
                    dto.setRoundMasterId(roundMaster.getId());

                } else {
                    dto.setRoundType("No Rounding");
                }
            } else {
                dto.setRoundType("No Rounding");
            }

            dto.setAddInGross(element.isGross());

            dto.setElementId(element.getId());

            finalDtoList.add(dto);

        }

        System.err.println("=====>" + finalDtoList.toString());
        return finalDtoList;

    }

    @Async
    private void sendNotification(UserMaster um, Long companyId, Long companyBranchId,
                                  EmployeeAppWorkFlowInstanceEntity employeeAppWorkFlowInstanceEntity, Employee em) {

        boolean isParrallel = employeeAppWorkFlowInstanceEntity.getRuleConfigurationMaster().getWorkflowProperties()
                .isParallelApproval();

        List<Long> toUsers = new ArrayList<>();
        Long id = employeeAppWorkFlowInstanceEntity.getId();

        if (isParrallel) {

            /* if parrallel then need to send notification to every level reivewers */
            employeeAppWorkFlowInstanceEntity.getReviewers().stream().map(x -> x.getSecond())
                    .forEach(user -> toUsers.addAll(user));

        } else {
            /* if in serial then need to send to first level reivewers */
            employeeAppWorkFlowInstanceEntity.getReviewers().stream().map(x -> x.getSecond()).findFirst()
                    .filter(user -> toUsers.addAll(user));

        }

        notificationMasterService.sendNotification(CommonConstant.GENERAL_APPLICATION, "Review Employee Application",
                "Request For " + "Employee" + " Application", um, userMasterRepository.findAllById(toUsers),
                "hrms/workflow/widget/employee/employeeView/?id="+id, false, companyId, companyBranchId);

    }

    private List<Pair<Integer, Set<Long>>> getEmployeeReviwerListForWorkFlowDto(Employee employee,
                                                                                WorkflowRuleConfigurationMaster ruleConfigurationMaster, HttpSession session) {

        EmpReportingOfficer officers = reportingOfficerRepository.findReviwerByEmpId(employee);
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");
        /*
         * WorkflowTypeEntity workflowTypeEntity =
         * workflowTypeRepository.findByTypeId(WorkflowType.LEAVE_APPLICATION)
         * .orElse(null);
         */

        List<String> reviewersLevelFromWrofkFlow = ruleConfigurationMaster.getWorkflowProperties().getReviewers();

        List<Pair<Integer, Set<Long>>> orderAndReviwers = new ArrayList<>();

        System.err.println("officers length" + officers);
        if (officers != null) {

            for (int i = 0; i < reviewersLevelFromWrofkFlow.size(); i++) {

                Pair<Integer, Set<Long>> pair = new Pair<>();

                pair.setFirst(i + 1);

                switch (reviewersLevelFromWrofkFlow.get(i)) {
                    case "DDO": {
                        if (officers.getDdo() != null) {
                            pair.setSecond(new HashSet<>(
                                    Arrays.asList(userMasterRepository.findByEmpId(officers.getDdo().getId()).getId())));
                        }
                        break;
                    }
                    case "DH": {
                        pair.setSecond(new HashSet<>(
                                Arrays.asList(userMasterRepository.findByEmpId(officers.getDh().getId()).getId())));
                        break;
                    }
                    case "HO": {
                        pair.setSecond(new HashSet<>(
                                Arrays.asList(userMasterRepository.findByEmpId(officers.getHo().getId()).getId())));
                        break;
                    }
                    case "HOD": {
                        pair.setSecond(new HashSet<>(
                                Arrays.asList(userMasterRepository.findByEmpId(officers.getHod().getId()).getId())));
                        break;
                    }
                    default:

//					WorkflowRoleMaster workflowRoleMaster = workflowRolesMasterRepository
//							.findAllByRoleNameIgnoreCase(reviewersLevelFromWrofkFlow.get(i));

                        WorkflowRoleMaster workflowRoleMaster = workflowRolesMasterRepository
                                .findAllByRoleNameIgnoreCaseAndCompanyIdAndCompanyBranchIdAndIsDeleteFalse(reviewersLevelFromWrofkFlow.get(i), companyId,
                                        companyBranchId);
                        Set<Long> defaultReviewers = new HashSet<>();
                        for (String id : workflowRoleMaster.getUserMasterId()) {
                            defaultReviewers.add(Long.parseLong(id));
                        }
                        pair.setSecond(defaultReviewers);
                }

                orderAndReviwers.add(pair);

            }
        }

        return orderAndReviwers;
    }

    JsonResponse saveEmpPersonalInfoTemp(EmpPersonalInfo employeePersonalObj, HttpServletRequest request,
                                         BindingResult result, Model model, Long userId) {

        JsonResponse res = new JsonResponse();

        EmpPersonalInfoTemp empPersonalInfoTemp = empPersonalInfoTempRepository
                .findByEmpIdAndIsApprovedFalse(employeePersonalObj.getEmp().getId());

        EmpPersonalInfoTemp empPersonalTempObj;
        if (empPersonalInfoTemp == null) {
            empPersonalTempObj = new EmpPersonalInfoTemp();
        } else {
            empPersonalTempObj = empPersonalInfoTemp;
        }

        String dobStr = request.getParameter("dateOfBirthStr");
        if (StringUtil.isNotEmpty(dobStr)) {
            employeePersonalObj.setDateOfBirth(DateUtil.convertStringToDate(dobStr, DateUtil.IST_DATE_FORMATE));
        }

        String domStr = request.getParameter("dateOfMarriageStr");
        if (StringUtil.isNotEmpty(domStr)) {
            employeePersonalObj.setDateOfMarriage(DateUtil.convertStringToDate(domStr, DateUtil.IST_DATE_FORMATE));
        }

        String doeStr = request.getParameter("dateOfExpiryStr");
        if (StringUtil.isNotEmpty(doeStr)) {
            employeePersonalObj.setDateOfExpiry(DateUtil.convertStringToDate(doeStr, DateUtil.IST_DATE_FORMATE));
        }

        String validUptoStr = request.getParameter("validUptoStr");
        if (StringUtil.isNotEmpty(validUptoStr)) {
            employeePersonalObj.setValidUpto(DateUtil.convertStringToDate(validUptoStr, DateUtil.IST_DATE_FORMATE));
        }

        EmpPersonalInfoValidator validatior = new EmpPersonalInfoValidator();
        validatior.validate(employeePersonalObj, result);
        if (result.hasErrors()) {
            model.addAttribute("employeePersonalObj", employeePersonalObj);

            res.setStatus("FAIL");

            Map<String, String> errors = new HashMap<String, String>();
            errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            res.setResult(errors);
            return res;
        }

        if (employeePersonalObj.getGender() != null) {
            empPersonalTempObj.setGender(employeePersonalObj.getGender());
        }

        if (employeePersonalObj.getMarriageStatus() != null) {
            empPersonalTempObj.setMarriageStatus(employeePersonalObj.getMarriageStatus());
        }

        if (employeePersonalObj.getDateOfBirth() != null) {
            empPersonalTempObj.setDateOfBirth(employeePersonalObj.getDateOfBirth());
        }

        if (employeePersonalObj.getDateOfMarriage() != null) {
            empPersonalTempObj.setDateOfMarriage(employeePersonalObj.getDateOfMarriage());
        }

        if (employeePersonalObj.getBirthPlace() != null) {
            empPersonalTempObj.setBirthPlace(employeePersonalObj.getBirthPlace());
        }

        if (employeePersonalObj.getCategory() != null) {
            empPersonalTempObj.setCategory(employeePersonalObj.getCategory());
        }

        if (employeePersonalObj.getUidNo() != null) {
            empPersonalTempObj.setUidNo(employeePersonalObj.getUidNo());
        }

        if (employeePersonalObj.getGpfNo() != null) {
            empPersonalTempObj.setGpfNo(employeePersonalObj.getGpfNo());
        }

        if (employeePersonalObj.getPranNo() != null) {
            empPersonalTempObj.setPranNo(employeePersonalObj.getPranNo());
        }

        if (employeePersonalObj.getPfNo() != null) {
            empPersonalTempObj.setPfNo(employeePersonalObj.getPfNo());
        }

        if (employeePersonalObj.getReligion() == null || employeePersonalObj.getReligion().getId() == null) {
            empPersonalTempObj.setReligion(null);
        } else {
            empPersonalTempObj.setReligion(employeePersonalObj.getReligion());
        }

        if (employeePersonalObj.getCast() == null || employeePersonalObj.getCast().getId() == null) {
            empPersonalTempObj.setCast(null);
        } else {
            empPersonalTempObj.setCast(employeePersonalObj.getCast());
        }

        if (employeePersonalObj.getCommCategoryRef() != null) {
            empPersonalTempObj.setCommCategoryRef(employeePersonalObj.getCommCategoryRef());
        }

        if (employeePersonalObj.getCountry() == null || employeePersonalObj.getCountry().getId() == null) {
            empPersonalTempObj.setCountry(null);
        } else {
            empPersonalTempObj.setCountry(employeePersonalObj.getCountry());
        }

        if (employeePersonalObj.getPliNo() != null) {
            empPersonalTempObj.setPliNo(employeePersonalObj.getPliNo());
        }

        if (employeePersonalObj.getHobbies() != null) {
            empPersonalTempObj.setHobbies(employeePersonalObj.getHobbies());
        }

        if (employeePersonalObj.getBank() != null || employeePersonalObj.getBank().getId() != null) {
            empPersonalTempObj.setBank(employeePersonalObj.getBank());
        }

        if (employeePersonalObj.getBankBranch() != null || employeePersonalObj.getBankBranch().getId() != null) {
            empPersonalTempObj.setBankBranch(employeePersonalObj.getBankBranch());
        }

        if (employeePersonalObj.getBankAccNo() != null) {
            empPersonalTempObj.setBankAccNo(employeePersonalObj.getBankAccNo());
        }

        if (StringUtil.isNotEmpty(employeePersonalObj.getIfscCode())) {
            empPersonalTempObj.setIfscCode(employeePersonalObj.getIfscCode());
        }

        if (StringUtil.isNotEmpty(employeePersonalObj.getBsrCode())) {
            empPersonalTempObj.setBsrCode(employeePersonalObj.getBsrCode());
        }

        if (StringUtil.isNotEmpty(employeePersonalObj.getBsrCode())) {
            empPersonalTempObj.setPassPortNo(employeePersonalObj.getPassPortNo());
        }

        if (employeePersonalObj.getDateOfExpiry() != null) {
            empPersonalTempObj.setDateOfExpiry(employeePersonalObj.getDateOfExpiry());
        }

        if (StringUtil.isNotEmpty(employeePersonalObj.getVisaDetail())) {
            empPersonalTempObj.setVisaDetail(employeePersonalObj.getVisaDetail());
        }

        if (StringUtil.isNotEmpty(employeePersonalObj.getDrivingLicenseNo())) {
            empPersonalTempObj.setDrivingLicenseNo(employeePersonalObj.getDrivingLicenseNo());
        }

        if (StringUtil.isNotEmpty(employeePersonalObj.getLicenseIssuedFor())) {
            empPersonalTempObj.setLicenseIssuedFor(employeePersonalObj.getLicenseIssuedFor());
        }

        if (employeePersonalObj.getValidUpto() != null) {
            empPersonalTempObj.setValidUpto(employeePersonalObj.getValidUpto());
        }

        if (employeePersonalObj.getState() == null || employeePersonalObj.getState().getId() == null) {
            empPersonalTempObj.setState(null);
        } else {
            empPersonalTempObj.setState(employeePersonalObj.getState());
        }

        if (request.getParameter("govtVehicle") != null) {
            empPersonalTempObj.setDtlGovtVehicle(employeePersonalObj.getDtlGovtVehicle());
            empPersonalTempObj.setIsgovtVehicleUseOD(true);
            empPersonalTempObj.setGovtVehicle(request.getParameter("govtVehicle"));

        } else {
            empPersonalTempObj.setIsgovtVehicleUseOD(false);
            empPersonalTempObj.setDtlGovtVehicle(null);
            empPersonalTempObj.setGovtVehicle(null);
        }

        if (request.getParameter("isResidentOtherCountry") != null) {
            if (employeePersonalObj.getOtherCountry() == null
                    || employeePersonalObj.getOtherCountry().getId() == null) {
                empPersonalTempObj.setOtherCountry(null);
            }
            empPersonalTempObj.setOtherCountryAdd(employeePersonalObj.getOtherCountryAdd());
            String dateOfMigrationStr = request.getParameter("dateOfMigrationStr");
            if (StringUtil.isNotEmpty(dateOfMigrationStr)) {
                empPersonalTempObj.setDateOfMigration(
                        DateUtil.convertStringToDate(dateOfMigrationStr, DateUtil.IST_DATE_FORMATE));
            }
            empPersonalTempObj.setIsResidentOtherCountry(request.getParameter("isResidentOtherCountry"));
            empPersonalTempObj.setOtherCountry(employeePersonalObj.getOtherCountry());
        } else {
            empPersonalTempObj.setDateOfMigration(null);
            empPersonalTempObj.setOtherCountry(null);
            empPersonalTempObj.setOtherCountryAdd(null);
        }

        if (request.getParameter("isAnyDisciplinaryProceding") != null) {
            empPersonalTempObj.setDisciplinaryProcedingDtl(employeePersonalObj.getDisciplinaryProcedingDtl());
            empPersonalTempObj.setIsAnyDisciplinaryProceding(request.getParameter("isAnyDisciplinaryProceding"));
        } else {
            empPersonalTempObj.setDisciplinaryProcedingDtl(null);
            empPersonalTempObj.setIsAnyDisciplinaryProceding(null);
        }

        if (StringUtil.isNotEmpty(employeePersonalObj.getAddiInfo())) {
            empPersonalTempObj.setAddiInfo(employeePersonalObj.getAddiInfo());
        }

        empPersonalTempObj.setEmp(employeePersonalObj.getEmp());

        empPersonalTempObj.setDrivingLicenseNo(employeePersonalObj.getDrivingLicenseNo());
        empPersonalTempObj.setLicenseIssuedFor(employeePersonalObj.getLicenseIssuedFor());
        empPersonalTempObj.setValidUpto(employeePersonalObj.getValidUpto());
        //empPersonalTempObj.setLicenseIssuedForStr(employeePersonalObj.getLicenseIssuedForStr());
        empPersonalTempObj.setAddiInfo(employeePersonalObj.getAddiInfo());
        //empPersonalTempObj.setDisciplinaryProcedingDtl(employeePersonalObj.getDisciplinaryProcedingDtl());
        //empPersonalTempObj.setDateOfMigration(employeePersonalObj.getDateOfMigration());
        empPersonalTempObj.setMarriageStatus(employeePersonalObj.getMarriageStatus());
        empPersonalTempObj.setDateOfMarriage(employeePersonalObj.getDateOfMarriage());
        empPersonalTempObj.setCommCategoryRef(employeePersonalObj.getCommCategoryRef());
        empPersonalTempObj.setPassPortNo(employeePersonalObj.getPassPortNo());
        empPersonalTempObj.setDateOfExpiry(employeePersonalObj.getDateOfExpiry());

        if (empPersonalTempObj.getId() == null) {

            EmpPersonalInfoTemp empPersonal = empPersonalInfoTempRepository.save(empPersonalTempObj);
            if (empPersonal != null) {
                EmpPersonalInfoTemp empObj = (EmpPersonalInfoTemp) empPersonal;
                res.setObj(empObj);
                res.setStatus("SUCCESS");
            }
            auditTrailService.saveAuditTrailData("Employee personal into temp", "Save", "Admin",
                    NotificationModule.EMPLOYEE_PERSONAL_INFO, NotificationAction.ADD, "/save", userId);
        } else {

            EmpPersonalInfoTemp empPersonal = empPersonalInfoTempRepository.save(empPersonalTempObj);
            if (empPersonal != null) {
                EmpPersonalInfoTemp empObj = (EmpPersonalInfoTemp) empPersonal;
                res.setObj(empObj);
                res.setStatus("UPDATE");
            }
            auditTrailService.saveAuditTrailData("Employee personal into temp", "Update", "Admin",
                    NotificationModule.EMPLOYEE_PERSONAL_INFO, NotificationAction.ADD, "/save", userId);
        }
        return res;
    }

    JsonResponse compareFieldsEmpPersonalInfoTemp(EmpPersonalInfo employeePersonalObj, HttpServletRequest request,
                                                  BindingResult result, Model model, Long userId) throws IllegalAccessException {

        JsonResponse res = new JsonResponse();

        EmpPersonalInfo empPersonal = empPersonalInfoRepository.findByEmpId(employeePersonalObj.getEmp().getId());

        EmpPersonalInfoTemp empPersonalInfoTemp = empPersonalInfoTempRepository
                .findByEmpIdAndIsApprovedFalse(employeePersonalObj.getEmp().getId());

        EmpPersonalInfoTemp empPersonalTempObj;
        if (empPersonalInfoTemp == null) {
            empPersonalTempObj = new EmpPersonalInfoTemp();
        } else {
            empPersonalTempObj = empPersonalInfoTemp;
        }

        // List<String> values = getDifference(empPersonal, employeePersonalObj);

        String dobStr = request.getParameter("dateOfBirthStr");
        if (StringUtil.isNotEmpty(dobStr)) {
            employeePersonalObj.setDateOfBirth(DateUtil.convertStringToDate(dobStr, DateUtil.IST_DATE_FORMATE));
        }

        String domStr = request.getParameter("dateOfMarriageStr");
        if (StringUtil.isNotEmpty(domStr)) {
            employeePersonalObj.setDateOfMarriage(DateUtil.convertStringToDate(domStr, DateUtil.IST_DATE_FORMATE));
        }

        String doeStr = request.getParameter("dateOfExpiryStr");
        if (StringUtil.isNotEmpty(doeStr)) {
            employeePersonalObj.setDateOfExpiry(DateUtil.convertStringToDate(doeStr, DateUtil.IST_DATE_FORMATE));
        }

        String validUptoStr = request.getParameter("validUptoStr");
        if (StringUtil.isNotEmpty(validUptoStr)) {
            employeePersonalObj.setValidUpto(DateUtil.convertStringToDate(validUptoStr, DateUtil.IST_DATE_FORMATE));
        }

        String dateOfMigrationStr = request.getParameter("dateOfMigrationStr");
        if (StringUtil.isNotEmpty(dateOfMigrationStr)) {
            employeePersonalObj.setDateOfMigration(DateUtil.convertStringToDate(dateOfMigrationStr, DateUtil.IST_DATE_FORMATE));
        }

        EmpPersonalInfoValidator validatior = new EmpPersonalInfoValidator();
        validatior.validate(employeePersonalObj, result);
        if (result.hasErrors()) {
            model.addAttribute("employeePersonalObj", employeePersonalObj);

            res.setStatus("FAIL");

            Map<String, String> errors = new HashMap<String, String>();
            errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            res.setResult(errors);
            return res;
        }

//		if (employeePersonalObj.getDateOfBirth() != null && empPersonal.getDateOfBirth() != null) {
//			if ((employeePersonalObj.getDateOfBirth().compareTo(empPersonal.getDateOfBirth()) > 0)
//					&& (employeePersonalObj.getDateOfBirth().compareTo(empPersonal.getDateOfBirth()) < 0)) {
//				empPersonalTempObj.setDateOfBirth(null);
//			} else {
//				empPersonalTempObj.setDateOfBirth(employeePersonalObj.getDateOfBirth());
//			}
//		} else if (employeePersonalObj.getDateOfBirth() == null && empPersonal.getDateOfBirth() != null) {
//			empPersonalTempObj.setDateOfBirth(null);
//		} else if (employeePersonalObj.getDateOfBirth() != null && empPersonal.getDateOfBirth() == null) {
//			empPersonalTempObj.setDateOfBirth(employeePersonalObj.getDateOfBirth());
//		}

        if(!Objects.equal(employeePersonalObj.getDateOfBirth() != null ? employeePersonalObj.getDateOfBirth() : "",
                empPersonal.getDateOfBirth() != null ? empPersonal.getDateOfBirth() : "")) {
            empPersonalTempObj.setDateOfBirth(employeePersonalObj.getDateOfBirth());
        }


//		if (employeePersonalObj.getDateOfMarriage() != null && empPersonal.getDateOfMarriage() != null) {
//			if ((employeePersonalObj.getDateOfMarriage().compareTo(empPersonal.getDateOfMarriage()) > 0)
//					&& (employeePersonalObj.getDateOfMarriage().compareTo(empPersonal.getDateOfMarriage()) < 0)) {
//				empPersonalTempObj.setDateOfMarriage(null);
//			} else {
//				empPersonalTempObj.setDateOfMarriage(employeePersonalObj.getDateOfMarriage());
//			}
//		} else if (employeePersonalObj.getDateOfMarriage() == null && empPersonal.getDateOfMarriage() != null) {
//			empPersonalTempObj.setDateOfMarriage(null);
//		} else if (employeePersonalObj.getDateOfMarriage() != null && empPersonal.getDateOfMarriage() == null) {
//			empPersonalTempObj.setDateOfMarriage(employeePersonalObj.getDateOfMarriage());
//		}

        if(!Objects.equal(employeePersonalObj.getDateOfMarriage() != null ? employeePersonalObj.getDateOfMarriage() : "",
                empPersonal.getDateOfMarriage() != null ? empPersonal.getDateOfMarriage() : "")) {
            empPersonalTempObj.setDateOfMarriage(employeePersonalObj.getDateOfMarriage());
        }

        if (Objects.equal(employeePersonalObj.getGender(), empPersonal.getGender())) {
            empPersonalTempObj.setGender(null);
        } else {
            empPersonalTempObj.setGender(employeePersonalObj.getGender());
        }

        if (Objects.equal(employeePersonalObj.getMarriageStatus(), empPersonal.getMarriageStatus())) {
            empPersonalTempObj.setMarriageStatus(null);
        } else {
            empPersonalTempObj.setMarriageStatus(employeePersonalObj.getMarriageStatus());
        }

        if (Objects.equal(employeePersonalObj.getBirthPlace(), empPersonal.getBirthPlace())) {
            empPersonalTempObj.setBirthPlace(null);
        } else {
            empPersonalTempObj.setBirthPlace(employeePersonalObj.getBirthPlace());
        }

        if (Objects.equal(employeePersonalObj.getCategory(), empPersonal.getCategory())) {
            empPersonalTempObj.setCategory(null);
        } else {
            empPersonalTempObj.setCategory(employeePersonalObj.getCategory());
        }

        if (Objects.equal(employeePersonalObj.getUidNo(), empPersonal.getUidNo())) {
            empPersonalTempObj.setUidNo(null);
        } else {
            empPersonalTempObj.setUidNo(employeePersonalObj.getUidNo());
        }

        if (Objects.equal(employeePersonalObj.getGpfNo(), empPersonal.getGpfNo())) {
            empPersonalTempObj.setGpfNo(null);
        } else {
            empPersonalTempObj.setGpfNo(employeePersonalObj.getGpfNo());
        }

        if (Objects.equal(employeePersonalObj.getPranNo(), empPersonal.getPranNo())) {
            empPersonalTempObj.setPranNo(null);
        } else {
            empPersonalTempObj.setPranNo(employeePersonalObj.getPranNo());
        }

        if (Objects.equal(employeePersonalObj.getPfNo(), empPersonal.getPfNo())) {
            empPersonalTempObj.setPfNo(null);
        } else {
            empPersonalTempObj.setPfNo(employeePersonalObj.getPfNo());
        }

        if (employeePersonalObj.getReligion() != null && empPersonal.getReligion() != null) {
            if (Objects.equal(employeePersonalObj.getReligion().getId(), empPersonal.getReligion().getId())) {
                empPersonalTempObj.setReligion(null);
            } else {
                if (employeePersonalObj.getReligion().getId() != null) {
                    empPersonalTempObj.setReligion(employeePersonalObj.getReligion());
                } else {
                    empPersonalTempObj.setReligion(null);
                }
            }
        } else if (employeePersonalObj.getReligion().getId() != null && empPersonal.getReligion() == null) {
            empPersonalTempObj.setReligion(employeePersonalObj.getReligion());
        } else if (employeePersonalObj.getReligion().getId() == null && empPersonal.getReligion() != null) {
            empPersonalTempObj.setReligion(null);
        } else {
            empPersonalTempObj.setReligion(null);
        }

        if (employeePersonalObj.getCast() != null && empPersonal.getCast() != null) {
            if (Objects.equal(employeePersonalObj.getCast().getId(), empPersonal.getCast().getId())) {
                empPersonalTempObj.setCast(null);
            } else {
                if (employeePersonalObj.getCast().getId() != null) {
                    empPersonalTempObj.setCast(employeePersonalObj.getCast());
                } else {
                    empPersonalTempObj.setCast(null);
                }
            }
        } else if (employeePersonalObj.getCast().getId() != null && empPersonal.getCast() == null) {
            empPersonalTempObj.setCast(employeePersonalObj.getCast());
        } else if (employeePersonalObj.getCast().getId() == null && empPersonal.getCast() != null) {
            empPersonalTempObj.setCast(null);
        } else {
            empPersonalTempObj.setCast(null);
        }

        if (Objects.equal(employeePersonalObj.getCommCategoryRef(), empPersonal.getCommCategoryRef())) {
            empPersonalTempObj.setCommCategoryRef(null);
        } else {
            empPersonalTempObj.setCommCategoryRef(employeePersonalObj.getCommCategoryRef());
        }

        if (employeePersonalObj.getCountry() != null && empPersonal.getCountry() != null) {
            if (Objects.equal(employeePersonalObj.getCountry().getId(), empPersonal.getCountry().getId())) {
                empPersonalTempObj.setCountry(null);
            } else {
                if (employeePersonalObj.getCountry().getId() != null) {
                    empPersonalTempObj.setCountry(employeePersonalObj.getCountry());
                } else {
                    empPersonalTempObj.setCountry(null);
                }
            }
        } else if (employeePersonalObj.getCountry().getId() != null && empPersonal.getCountry() == null) {
            empPersonalTempObj.setCountry(employeePersonalObj.getCountry());
        } else if (employeePersonalObj.getCountry().getId() == null && empPersonal.getCountry() != null) {
            empPersonalTempObj.setCountry(null);
        } else {
            empPersonalTempObj.setCountry(null);
        }

        if (Objects.equal(employeePersonalObj.getPliNo(), empPersonal.getPliNo())) {
            empPersonalTempObj.setPliNo(null);
        } else {
            empPersonalTempObj.setPliNo(employeePersonalObj.getPliNo());
        }

        if (Objects.equal(employeePersonalObj.getHobbies(), empPersonal.getHobbies())) {
            empPersonalTempObj.setHobbies(null);
        } else {
            empPersonalTempObj.setHobbies(employeePersonalObj.getHobbies());
        }

        if (Objects.equal(employeePersonalObj.getBank().getId(), empPersonal.getBank().getId())) {
            empPersonalTempObj.setBank(null);
        } else {
            empPersonalTempObj.setBank(employeePersonalObj.getBank());
        }

        if (Objects.equal(employeePersonalObj.getBankBranch().getId(), empPersonal.getBankBranch().getId())) {
            empPersonalTempObj.setBankBranch(null);
        } else {
            empPersonalTempObj.setBankBranch(employeePersonalObj.getBankBranch());
        }

        if (Objects.equal(employeePersonalObj.getBankAccNo(), empPersonal.getBankAccNo())) {
            empPersonalTempObj.setBankAccNo(null);
        } else {
            empPersonalTempObj.setBankAccNo(employeePersonalObj.getBankAccNo());
        }

        if (Objects.equal(employeePersonalObj.getIfscCode(), empPersonal.getIfscCode())) {
            empPersonalTempObj.setIfscCode(null);
        } else {
            empPersonalTempObj.setIfscCode(employeePersonalObj.getIfscCode());
        }

        if (Objects.equal(employeePersonalObj.getBsrCode(), empPersonal.getBsrCode())) {
            empPersonalTempObj.setBsrCode(null);
        } else {
            empPersonalTempObj.setBsrCode(employeePersonalObj.getBsrCode());
        }

        if (Objects.equal(employeePersonalObj.getPassPortNo(), empPersonal.getPassPortNo())) {
            empPersonalTempObj.setPassPortNo(null);
        } else {
            empPersonalTempObj.setPassPortNo(employeePersonalObj.getPassPortNo());
        }

//		if (employeePersonalObj.getDateOfExpiry() != null && empPersonal.getDateOfExpiry() != null) {
//			if ((employeePersonalObj.getDateOfExpiry().compareTo(empPersonal.getDateOfExpiry()) > 0)
//					&& (employeePersonalObj.getDateOfExpiry().compareTo(empPersonal.getDateOfExpiry()) < 0)) {
//				empPersonalTempObj.setDateOfExpiry(null);
//			} else {
//				empPersonalTempObj.setDateOfExpiry(employeePersonalObj.getDateOfExpiry());
//			}
//		} else if (employeePersonalObj.getDateOfExpiry() == null && empPersonal.getDateOfExpiry() != null) {
//			empPersonalTempObj.setDateOfExpiry(null);
//		} else if (employeePersonalObj.getDateOfExpiry() != null && empPersonal.getDateOfExpiry() == null) {
//			empPersonalTempObj.setDateOfExpiry(employeePersonalObj.getDateOfExpiry());
//		}else {
//			empPersonalTempObj.setDateOfExpiry(null);
//		}

        if(!Objects.equal(employeePersonalObj.getDateOfExpiry() != null ? employeePersonalObj.getDateOfExpiry() : "",
                empPersonal.getDateOfExpiry() != null ? empPersonal.getDateOfExpiry() : "")) {
            empPersonalTempObj.setDateOfExpiry(employeePersonalObj.getDateOfExpiry());
        }

        if (Objects.equal(employeePersonalObj.getVisaDetail(), empPersonal.getVisaDetail())) {
            empPersonalTempObj.setVisaDetail(null);
        } else {
            empPersonalTempObj.setVisaDetail(employeePersonalObj.getVisaDetail());
        }

        if (Objects.equal(employeePersonalObj.getDrivingLicenseNo(), empPersonal.getDrivingLicenseNo())) {
            empPersonalTempObj.setDrivingLicenseNo(null);
        } else {
            empPersonalTempObj.setDrivingLicenseNo(employeePersonalObj.getDrivingLicenseNo());
        }

        if (Objects.equal(employeePersonalObj.getLicenseIssuedFor(), empPersonal.getLicenseIssuedFor())) {
            empPersonalTempObj.setLicenseIssuedFor(null);
        } else {
            empPersonalTempObj.setLicenseIssuedFor(employeePersonalObj.getLicenseIssuedFor());
        }

//		if (employeePersonalObj.getValidUpto() != null && empPersonal.getValidUpto() != null) {
//			if ((employeePersonalObj.getValidUpto().compareTo(empPersonal.getValidUpto()) > 0)
//					&& (employeePersonalObj.getValidUpto().compareTo(empPersonal.getValidUpto()) < 0)) {
//				empPersonalTempObj.setValidUpto(employeePersonalObj.getValidUpto());
//			} else {
//				empPersonalTempObj.setValidUpto(null);
//			}
//		} else if (employeePersonalObj.getValidUpto() == null && empPersonal.getValidUpto() != null) {
//			empPersonalTempObj.setValidUpto(null);
//		} else if (employeePersonalObj.getValidUpto() != null && empPersonal.getValidUpto() == null) {
//			empPersonalTempObj.setValidUpto(employeePersonalObj.getValidUpto());
//		} else {
//			empPersonalTempObj.setValidUpto(null);
//		}

        if(!Objects.equal(employeePersonalObj.getValidUpto() != null ? employeePersonalObj.getValidUpto() : "",
                empPersonal.getValidUpto() != null ? empPersonal.getValidUpto() : "")) {
            empPersonalTempObj.setValidUpto(employeePersonalObj.getValidUpto());
        }

        if (employeePersonalObj.getState() != null && empPersonal.getState() != null) {
            if (Objects.equal(employeePersonalObj.getState().getId(), empPersonal.getState().getId())) {
                empPersonalTempObj.setState(null);
            } else {
                if (employeePersonalObj.getState().getId() != null) {
                    empPersonalTempObj.setState(employeePersonalObj.getState());
                } else {
                    empPersonalTempObj.setState(null);
                }
            }
        } else if (employeePersonalObj.getState().getId() != null && empPersonal.getState() == null) {
            empPersonalTempObj.setState(employeePersonalObj.getState());
        } else if (employeePersonalObj.getState().getId() == null && empPersonal.getState() != null) {
            empPersonalTempObj.setState(null);
        }else {
            empPersonalTempObj.setState(null);
        }

        if (employeePersonalObj.getOtherCountry() != null && empPersonal.getOtherCountry() != null) {
            if (Objects.equal(employeePersonalObj.getOtherCountry().getId(), empPersonal.getOtherCountry().getId())) {
                empPersonalTempObj.setOtherCountry(null);
            } else {
                if (employeePersonalObj.getOtherCountry().getId() != null) {
                    empPersonalTempObj.setOtherCountry(employeePersonalObj.getOtherCountry());
                } else {
                    empPersonalTempObj.setOtherCountry(null);
                }
            }
        } else if (employeePersonalObj.getOtherCountry().getId() != null && empPersonal.getOtherCountry() == null) {
            empPersonalTempObj.setOtherCountry(employeePersonalObj.getOtherCountry());
        } else if (employeePersonalObj.getOtherCountry().getId() == null && empPersonal.getOtherCountry() != null) {
            empPersonalTempObj.setOtherCountry(null);
        }else {
            empPersonalTempObj.setOtherCountry(null);
        }

        if (request.getParameter("govtVehicle") != null) {
            if(!employeePersonalObj.getDtlGovtVehicle().equals(empPersonal.getDtlGovtVehicle())) {
                empPersonalTempObj.setDtlGovtVehicle(employeePersonalObj.getDtlGovtVehicle());
                empPersonalTempObj.setIsgovtVehicleUseOD(true);
                if (Objects.equal(request.getParameter("govtVehicle"),empPersonal.getGovtVehicle())) {
                    empPersonalTempObj.setGovtVehicle(null);
                } else {
                    empPersonalTempObj.setGovtVehicle(request.getParameter("govtVehicle"));
                }

            } else {
                empPersonalTempObj.setDtlGovtVehicle(null);
                empPersonalTempObj.setGovtVehicle(null);
                empPersonalTempObj.setIsgovtVehicleUseOD(empPersonal.isIsgovtVehicleUseOD());
            }
        }else {
            empPersonalTempObj.setDtlGovtVehicle(null);
            empPersonalTempObj.setGovtVehicle("N");
            empPersonalTempObj.setIsgovtVehicleUseOD(false);
        }

        System.out.println("resident >> "+request.getParameter("isResidentOtherCountry"));
        System.out.println("otherCountry.id >> "+request.getParameter("otherCountry.id"));
        System.out.println("isAnyDisciplinaryProceding >> "+request.getParameter("isAnyDisciplinaryProceding"));
        if (request.getParameter("isResidentOtherCountry") != null) {

            if (request.getParameter("otherCountry.id") != null && empPersonal.getOtherCountry() != null) {
                if (Objects.equal(employeePersonalObj.getOtherCountry().getId(),
                        empPersonal.getOtherCountry().getId())) {
                    empPersonalTempObj.setOtherCountry(null);
                } else {
                    if (employeePersonalObj.getOtherCountry().getId() != null) {
                        empPersonalTempObj.setOtherCountry(employeePersonalObj.getOtherCountry());
                    } else {
                        empPersonalTempObj.setOtherCountry(null);
                    }
                }
            } else if (request.getParameter("otherCountry.id") != null && empPersonal.getOtherCountry() == null) {
                empPersonalTempObj.setOtherCountry(employeePersonalObj.getOtherCountry());
            } else if (request.getParameter("otherCountry.id") == null && empPersonal.getOtherCountry() != null) {
                empPersonalTempObj.setOtherCountry(null);
            }else {
                empPersonalTempObj.setOtherCountry(null);
            }


            if (Objects.equal(employeePersonalObj.getOtherCountryAdd(),empPersonal.getOtherCountryAdd())) {
                empPersonalTempObj.setOtherCountryAdd(null);
            } else {
                empPersonalTempObj.setOtherCountryAdd(employeePersonalObj.getOtherCountryAdd());
            }

            if (employeePersonalObj.getDateOfMigration() != null && empPersonal.getDateOfMigration() != null) {
                if ((employeePersonalObj.getDateOfMigration().compareTo(empPersonal.getDateOfMigration()) > 0)
                        && (employeePersonalObj.getDateOfMigration().compareTo(empPersonal.getDateOfMigration()) < 0)) {
                    empPersonalTempObj.setDateOfMigration(employeePersonalObj.getDateOfMigration());
                } else {
                    empPersonalTempObj.setDateOfMigration(null);
                }
            } else if (employeePersonalObj.getDateOfMigration() == null && empPersonal.getDateOfMigration() != null) {
                empPersonalTempObj.setDateOfMigration(null);
            } else if (employeePersonalObj.getDateOfMigration() != null && empPersonal.getDateOfMigration() == null) {
                empPersonalTempObj.setDateOfMigration(employeePersonalObj.getDateOfMigration());
            }


            if (Objects.equal(request.getParameter("isResidentOtherCountry"),empPersonal.getIsResidentOtherCountry())) {
                empPersonalTempObj.setIsResidentOtherCountry(null);
            } else {
                empPersonalTempObj.setIsResidentOtherCountry(request.getParameter("isResidentOtherCountry"));
            }

        } else {
            empPersonalTempObj.setDateOfMigration(null);
            empPersonalTempObj.setOtherCountry(null);
            empPersonalTempObj.setOtherCountryAdd(null);
            empPersonalTempObj.setIsResidentOtherCountry("N");
        }

        if (request.getParameter("isAnyDisciplinaryProceding") != null) {
            if (Objects.equal(employeePersonalObj.getDisciplinaryProcedingDtl(),
                    empPersonal.getDisciplinaryProcedingDtl())) {
                empPersonalTempObj.setDisciplinaryProcedingDtl(null);
            } else {
                empPersonalTempObj.setDisciplinaryProcedingDtl(employeePersonalObj.getDisciplinaryProcedingDtl());
                if (Objects.equal(request.getParameter("isAnyDisciplinaryProceding"),empPersonal.getIsAnyDisciplinaryProceding())) {
                    empPersonalTempObj.setIsAnyDisciplinaryProceding(null);
                } else {
                    empPersonalTempObj.setIsAnyDisciplinaryProceding(request.getParameter("isAnyDisciplinaryProceding"));
                }

            }
        } else {
            empPersonalTempObj.setDisciplinaryProcedingDtl(null);
            empPersonalTempObj.setIsAnyDisciplinaryProceding("N");
        }

        if (Objects.equal(employeePersonalObj.getAddiInfo(), empPersonal.getAddiInfo())) {
            empPersonalTempObj.setAddiInfo(null);
        } else {
            empPersonalTempObj.setAddiInfo(employeePersonalObj.getAddiInfo());
        }

        empPersonalTempObj.setEmp(employeePersonalObj.getEmp());

        empPersonalTempObj.setEmpPersonalInfoId(empPersonal.getId());
        if (empPersonalTempObj.getId() == null) {

            EmpPersonalInfoTemp empPersonalTemp = empPersonalInfoTempRepository.save(empPersonalTempObj);
            if (empPersonal != null) {
                EmpPersonalInfoTemp empObj = (EmpPersonalInfoTemp) empPersonalTemp;
                res.setObj(empObj);
                res.setStatus("SUCCESS");
            }
            auditTrailService.saveAuditTrailData("Employee personal into in temp", "Save", "Admin",
                    NotificationModule.EMPLOYEE_PERSONAL_INFO, NotificationAction.ADD, "/save", userId);
        } else {

            EmpPersonalInfoTemp em = empPersonalInfoTempRepository.save(empPersonalTempObj);
            if (em != null) {
                res.setStatus("UPDATE");
            }
            auditTrailService.saveAuditTrailData("Employee personal into in temp", "Update", "Admin",
                    NotificationModule.EMPLOYEE_PERSONAL_INFO, NotificationAction.UPDATE, "/save", userId);
        }
        return res;
    }

    JsonResponse saveEmpHealthDtlTemp(EmpHealthDtl employeeHealthDtlObj, HttpServletRequest request,
                                      BindingResult result, Model model, Long userId, HttpSession session) {

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");
        Long companyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

//		EmpHealthDtlTemp empHealthDtlTemp = empHealthTempRepository.findByEmpIdAndIsApprovedFalse(employeeHealthDtlObj.getEmp().getId());
//
//		if(empHealthDtlTemp == null) {

        EmpHealthDtlTemp empHealthDtlTempObj = new EmpHealthDtlTemp();

        if (empHealthTempRepository.findByEmpIdAndIsApprovedFalse(employeeHealthDtlObj.getEmp().getId()) != null) {
            empHealthDtlTempObj = empHealthTempRepository
                    .findByEmpIdAndIsApprovedFalse(employeeHealthDtlObj.getEmp().getId());
        }

        if (companyId != null) {
            Optional<CompanyMaster> cm = companyMasterRepository.findById(companyId);
            if (cm.isPresent()) {
                empHealthDtlTempObj.setCompany(cm.get());
            }
        }
        if (companyBranchId != null) {
            Optional<CompanyBranchMaster> cbm = companyBranchMasterRepository.findById(companyBranchId);
            if (cbm.isPresent()) {
                empHealthDtlTempObj.setCompanyBranch(cbm.get());
            }
        }

        if (request.getParameter("isPhysicallyHandicapped") != null) {
            empHealthDtlTempObj.setPhysicallyHandicapped(true);
            empHealthDtlTempObj.setDisabilityType(employeeHealthDtlObj.getDisabilityType());
        } else {
            empHealthDtlTempObj.setPhysicallyHandicapped(false);
            empHealthDtlTempObj.setDisabilityType(null);
        }

        if (request.getParameter("isFamilyPlanningAllowance") != null)
            empHealthDtlTempObj.setFamilyPlanningAllowance(true);
        else
            empHealthDtlTempObj.setFamilyPlanningAllowance(false);

        empHealthDtlTempObj.setEmp(employeeHealthDtlObj.getEmp());
        empHealthDtlTempObj.setCreatedBy(userId);
        empHealthDtlTempObj.setUpdatedBy(userId);
        empHealthDtlTempObj.setIpAddress(request.getRemoteAddr());
        empHealthDtlTempObj.setHeight(employeeHealthDtlObj.getHeight());
        empHealthDtlTempObj.setWeight(employeeHealthDtlObj.getWeight());
        empHealthDtlTempObj.setBloodGroup(employeeHealthDtlObj.getBloodGroup());
        empHealthDtlTempObj.setIdentificationMarkFirst(employeeHealthDtlObj.getIdentificationMarkFirst());
        empHealthDtlTempObj.setIdentificationMarkSecond(employeeHealthDtlObj.getIdentificationMarkSecond());

        EmpHealthDtlTemp cbm = empHealthTempRepository.save(empHealthDtlTempObj);
        if (cbm != null) {
            res.setObj(empHealthDtlTempObj);
            res.setStatus("SUCCESS");
        }
        return res;
    }

    @ResponseBody
    @GetMapping(value = "/sendForApproval")
    public JsonResponse sendApplicationForApproval(Model model, HttpServletRequest request, HttpSession session) {

        JsonResponse res = new JsonResponse();
        try {

            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            Employee emp = employeeRepository.findById(um.getEmpId().getId()).get();

            PassEventDto eventDto = new PassEventDto();
            eventDto.setWorkflowType(WorkflowType.EMPLOYEE_APPLICATION);
            eventDto.setWorkflowInstanceId(emp.getEmployeeAppWorkflow());
            eventDto.setEvent("E_RESET");
            eventDto.setActionBy(um.getId());
            List<EventResultDto> employee = employeeAppService.resetStateMachine(eventDto);

            callEvent(emp.getEmployeeAppWorkflow(), "E_CREATE", "Changes Done By User", request, session);

//			Integer typeId= WorkflowType.EMPLOYEE_APPLICATION.getTypeId();
//
//			WorkflowInstanceEntity wfInstance = workflowInstanceRepository.findByComIdAndBranchIdAndTypeIdAndEmployee(
//					companyId, Integer.valueOf(companyBranchId.intValue()), WorkflowType.EMPLOYEE_APPLICATION, emp.getEmployeeAppWorkflow());

//			WorkflowInstanceEntity wfInstance1 = workflowInstanceRepository.findByComIdAndBranchIdAndTypeIdAndEmpIdId(
//					companyId, Integer.valueOf(companyBranchId.intValue()), typeId, emp.getEmployeeAppWorkflow());

//			String lastForward = wfInstance.getLastForwardedBy() + "";
//			if (StringUtil.isNotEmpty(lastForward)) {
//
//				callEvent(emp.getEmployeeAppWorkflow(), "E_CREATE", "Changes Done By User", request, session);
//
//			} else {
//				PassEventDto eventDto = new PassEventDto();
//				eventDto.setWorkflowType(WorkflowType.EMPLOYEE_APPLICATION);
//				eventDto.setWorkflowInstanceId(emp.getEmployeeAppWorkflow());
//				eventDto.setEvent("E_RESET");
//				eventDto.setActionBy(um.getId());
//				List<EventResultDto> employee = employeeAppService.resetStateMachine(eventDto);
//
//				callEvent(emp.getEmployeeAppWorkflow(), "E_CREATE", "Changes Done By User", request, session);
            //}

            res.setStatus("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String callEvent(Long employeeWorkFlowId, String event, String comment, HttpServletRequest request,
                            HttpSession session) throws Exception {

        try {

            System.err.println("employeeWorkFlowId : " + employeeWorkFlowId + "\n" + "event : " + event
                    + "\n comment : " + comment);

            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            PassEventDto dto = new PassEventDto();

            dto.setActionBy(um.getId());
            dto.setEvent(event);
            dto.setWorkflowType(WorkflowType.EMPLOYEE_APPLICATION);
            dto.setWorkflowInstanceId(employeeWorkFlowId);
            dto.setComment(comment);

            System.err.println("Call Event DTO : " + dto);
            List<EventResponseDto> responsedto = employeeAppService.passEventToSM(dto);

//			WorkflowRuleConfigurationMaster ruleConfigurationMaster = configuratiionMasterRepository
//					.findByWorkflowTypeAndEmployeeIdToGetLatestEmployeeRule(companyId, companyBranchId,
//							WorkflowType.EMPLOYEE_APPLICATION.getTypeId(),um.getId().toString());
//
//			getEmployeeReviwerListForWorkFlowDto(um.getEmpId(), ruleConfigurationMaster);

            System.err.println("RESULT DTO : " + responsedto);

            if (responsedto.size() != 0) {

                return "SUCCESS";
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }

        throw new Exception();

    }

    JsonResponse saveEmpContactInfoTemp(EmpContactDtl employeeContactObj, HttpServletRequest request,
                                        BindingResult result, Model model, Long userId) {

        JsonResponse res = new JsonResponse();

        EmpContactDtlTemp empContactInfoTemp = empContactDtlTempRepository
                .findByEmpIdAndIsApprovedFalse(employeeContactObj.getEmp().getId());

        EmpContactDtlTemp empContactTempObj;
        if (empContactInfoTemp == null) {
            empContactTempObj = new EmpContactDtlTemp();
        } else {
            empContactTempObj = empContactInfoTemp;
        }

        if (StringUtil.isNotEmpty(employeeContactObj.getWorkPhone())) {
            empContactTempObj.setWorkPhone(employeeContactObj.getWorkPhone());
        }else {
            empContactTempObj.setWorkPhone(null);
        }

        if (StringUtil.isNotEmpty(employeeContactObj.getExtension())) {
            empContactTempObj.setExtension(employeeContactObj.getExtension());
        }else {
            empContactTempObj.setExtension(null);
        }

        if (StringUtil.isNotEmpty(employeeContactObj.getWorkMobile())) {
            empContactTempObj.setWorkMobile(employeeContactObj.getWorkMobile());
        }else {
            empContactTempObj.setWorkMobile(null);
        }

        if (StringUtil.isNotEmpty(employeeContactObj.getHomePhone())) {
            empContactTempObj.setHomePhone(employeeContactObj.getHomePhone());
        }else {
            empContactTempObj.setHomePhone(null);
        }

        if (StringUtil.isNotEmpty(employeeContactObj.getHomeMobile())) {
            empContactTempObj.setHomeMobile(employeeContactObj.getHomeMobile());
        }else {
            empContactTempObj.setHomeMobile(null);
        }

        if (StringUtil.isNotEmpty(employeeContactObj.getCorporateEmail())) {
            empContactTempObj.setCorporateEmail(employeeContactObj.getCorporateEmail());
        }

        if (StringUtil.isNotEmpty(employeeContactObj.getPersonalEmail())) {
            empContactTempObj.setPersonalEmail(employeeContactObj.getPersonalEmail());
        }else {
            empContactTempObj.setPersonalEmail(null);
        }

        if (StringUtil.isNotEmpty(employeeContactObj.getSecondaryEmail())) {
            empContactTempObj.setSecondaryEmail(employeeContactObj.getSecondaryEmail());
        }else {
            empContactTempObj.setSecondaryEmail(null);
        }

        empContactTempObj.setEmp(employeeContactObj.getEmp());

        if (empContactTempObj.getId() == null) {

            EmpContactDtlTemp empContact = empContactDtlTempRepository.save(empContactTempObj);
            if (empContact != null) {
                EmpContactDtlTemp contactObj = (EmpContactDtlTemp) empContact;
                res.setObj(contactObj);
                res.setStatus("SUCCESS");
            }
            auditTrailService.saveAuditTrailData("Employee contact into temp", "Save", "Admin",
                    NotificationModule.EMPLOYEE_CONTACT_INFO, NotificationAction.ADD, "/save", userId);
        } else {

            EmpContactDtlTemp empContact = empContactDtlTempRepository.save(empContactTempObj);
            if (empContact != null) {
                EmpContactDtlTemp contactObj = (EmpContactDtlTemp) empContact;
                res.setObj(contactObj);
                res.setStatus("UPDATE");
            }
            auditTrailService.saveAuditTrailData("Employee contact into temp", "Update", "Admin",
                    NotificationModule.EMPLOYEE_CONTACT_INFO, NotificationAction.ADD, "/save", userId);
        }
        return res;
    }

    JsonResponse compareFieldsEmpContactInfoTemp(EmpContactDtl employeeContactObj, HttpServletRequest request,
                                                 BindingResult result, Model model, Long userId) throws IllegalAccessException {

        JsonResponse res = new JsonResponse();

        EmpContactDtl empContact = empContactRepository.findByEmpId(employeeContactObj.getEmp().getId());

        EmpContactDtlTemp empContactDtlTemp = empContactDtlTempRepository
                .findByEmpIdAndIsApprovedFalse(employeeContactObj.getEmp().getId());

        EmpContactDtlTemp empContactDtlTempObj;
        if (empContactDtlTemp == null) {
            empContactDtlTempObj = new EmpContactDtlTemp();
        } else {
            empContactDtlTempObj = empContactDtlTemp;
        }

        if (Objects.equal(empContact.getWorkPhone(), employeeContactObj.getWorkPhone())) {
            empContactDtlTempObj.setWorkPhone(null);
        } else {
            empContactDtlTempObj.setWorkPhone(employeeContactObj.getWorkPhone());
        }

        if (Objects.equal(empContact.getExtension(), employeeContactObj.getExtension())) {
            empContactDtlTempObj.setExtension(null);
        } else {
            empContactDtlTempObj.setExtension(employeeContactObj.getExtension());
        }

        if (Objects.equal(empContact.getWorkMobile(), employeeContactObj.getWorkMobile())) {
            empContactDtlTempObj.setWorkMobile(null);
        } else {
            empContactDtlTempObj.setWorkMobile(employeeContactObj.getWorkMobile());
        }

        if (Objects.equal(empContact.getHomePhone(), employeeContactObj.getHomePhone())) {
            empContactDtlTempObj.setHomePhone(null);
        } else {
            empContactDtlTempObj.setHomePhone(employeeContactObj.getHomePhone());
        }

        if (Objects.equal(empContact.getHomeMobile(), employeeContactObj.getHomeMobile())) {
            empContactDtlTempObj.setHomeMobile(null);
        } else {
            empContactDtlTempObj.setHomeMobile(employeeContactObj.getHomeMobile());
        }

        if (Objects.equal(empContact.getCorporateEmail(), employeeContactObj.getCorporateEmail())) {
            empContactDtlTempObj.setCorporateEmail(null);
        } else {
            empContactDtlTempObj.setCorporateEmail(employeeContactObj.getCorporateEmail());
        }

        if (Objects.equal(empContact.getPersonalEmail(), employeeContactObj.getPersonalEmail())) {
            empContactDtlTempObj.setPersonalEmail(null);
        } else {
            empContactDtlTempObj.setPersonalEmail(employeeContactObj.getPersonalEmail());
        }

        if (Objects.equal(empContact.getSecondaryEmail(), employeeContactObj.getSecondaryEmail())) {
            empContactDtlTempObj.setSecondaryEmail(null);
        } else {
            empContactDtlTempObj.setSecondaryEmail(employeeContactObj.getSecondaryEmail());
        }

        empContactDtlTempObj.setEmp(employeeContactObj.getEmp());

        empContactDtlTempObj.setEmpContactDtlId(employeeContactObj.getId());

        if (empContactDtlTempObj.getId() == null) {

            EmpContactDtlTemp empContactTemp = empContactDtlTempRepository.save(empContactDtlTempObj);
            if (empContactTemp != null) {
                EmpContactDtlTemp empObj = (EmpContactDtlTemp) empContactTemp;
                res.setObj(empObj);
                res.setStatus("SUCCESS");
            }
            auditTrailService.saveAuditTrailData("Employee contact into in temp", "Save", "Admin",
                    NotificationModule.EMPLOYEE_CONTACT_INFO, NotificationAction.ADD, "/save", userId);
        } else {

            EmpContactDtlTemp empContactTemp = empContactDtlTempRepository.save(empContactDtlTempObj);
            if (empContactTemp != null) {
                EmpContactDtlTemp empObj = (EmpContactDtlTemp) empContactTemp;
                res.setObj(empObj);
                res.setStatus("UPDATE");
            }
            auditTrailService.saveAuditTrailData("Employee contact into in temp", "Update", "Admin",
                    NotificationModule.EMPLOYEE_CONTACT_INFO, NotificationAction.UPDATE, "/save", userId);
        }
        return res;
    }

    JsonResponse saveEmpAddressInfoTemp(EmpAddressDtl employeeAddressObj, HttpServletRequest request,
                                        BindingResult result, Model model, Long userId) {

        JsonResponse res = new JsonResponse();

        EmpAddressDtlTemp empAddressTempObj;
        if (employeeAddressObj.getId() != null) {
            empAddressTempObj = empAddressDtlTempRepository.findById(employeeAddressObj.getId()).get();
        } else {
            empAddressTempObj = new EmpAddressDtlTemp();
        }

        if (employeeAddressObj.getAddType() != null) {
            empAddressTempObj.setAddType(employeeAddressObj.getAddType());
        }else {
            empAddressTempObj.setAddType(null);
        }

        if (employeeAddressObj.getAddressEmp() != null) {
            empAddressTempObj.setAddressEmp(employeeAddressObj.getAddressEmp());
        }

        if (employeeAddressObj.getCountry() != null) {
            empAddressTempObj.setCountry(employeeAddressObj.getCountry());
        }

        if (employeeAddressObj.getState() != null) {
            empAddressTempObj.setState(employeeAddressObj.getState());
        }

        if (employeeAddressObj.getDistrict() != null) {
            empAddressTempObj.setDistrict(employeeAddressObj.getDistrict());
        }

        if (employeeAddressObj.getCity() != null) {
            empAddressTempObj.setCity(employeeAddressObj.getCity());
        }

        if (employeeAddressObj.getPincode() != null) {
            empAddressTempObj.setPincode(employeeAddressObj.getPincode());
        }

        if (employeeAddressObj.getAllAddSame() != null) {
            empAddressTempObj.setAllAddSame(employeeAddressObj.getAllAddSame());
        }else {
            empAddressTempObj.setAllAddSame(null);
        }

        if (employeeAddressObj.getPropertyType() != null) {
            empAddressTempObj.setPropertyType(employeeAddressObj.getPropertyType());
        }else {
            empAddressTempObj.setPropertyType(null);
        }

        empAddressTempObj.setEmp(employeeAddressObj.getEmp());

        if (empAddressTempObj.getId() == null) {

            EmpAddressDtlTemp empAddress = empAddressDtlTempRepository.save(empAddressTempObj);
            if (empAddress != null) {
                EmpAddressDtlTemp contactObj = (EmpAddressDtlTemp) empAddress;
                res.setObj(contactObj);
                res.setStatus("SUCCESS");
            }
            auditTrailService.saveAuditTrailData("Employee address into temp", "Save", "Admin",
                    NotificationModule.EMPLOYEE_ADDRESS_INFO, NotificationAction.ADD, "/save", userId);
        } else {

            EmpAddressDtlTemp empAddress = empAddressDtlTempRepository.save(empAddressTempObj);
            if (empAddress != null) {
                EmpAddressDtlTemp contactObj = (EmpAddressDtlTemp) empAddress;
                res.setObj(contactObj);
                res.setStatus("UPDATE");
            }
            auditTrailService.saveAuditTrailData("Employee address into temp", "Update", "Admin",
                    NotificationModule.EMPLOYEE_ADDRESS_INFO, NotificationAction.ADD, "/save", userId);
        }
        return res;
    }

    JsonResponse compareFieldsEmpAddressInfoTemp(EmpAddressDtl employeeAddressObj, HttpServletRequest request,
                                                 BindingResult result, Model model, Long userId, HttpSession session) throws IllegalAccessException {

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");

//		EmpAddressDtl empAddress = empAddressRepository.findByEmployeeId(employeeAddressObj.getEmp().getId());
//
//		EmpAddressDtlTemp empAddresssInfoTemp = empAddressDtlTempRepository
//				.findByEmpAddressDtlMstIdAndIsApprovedFalse(empAddress.getId());
//
//		EmpAddressDtlTemp empAddressDtlTempObj;
//		if (empAddresssInfoTemp == null) {
//			empAddressDtlTempObj = new EmpAddressDtlTemp();
//		} else {
//			empAddressDtlTempObj = empAddresssInfoTemp;
//		}

        // ==============================

        EmpAddressDtl empAddress = null;

        EmpAddressDtlTemp empAddTemp = null;

        if (employeeAddressObj.getId() != null) {
            empAddress = empAddressRepository.findById(employeeAddressObj.getId()).get();

            empAddTemp = empAddressDtlTempRepository.findByEmpAddressDtlMstIdAndIsApprovedFalse(empAddress.getId());
        } else {

            empAddress = new EmpAddressDtl();
            empAddress.setCountry(new CountryMaster());
            empAddress.setState(new StateMaster());
            empAddress.setCity(new CityMaster());
            empAddress.setDistrict(new DistrictMaster());
        }

        EmpAddressDtlTemp empAddressDtlTempObj;
        if (empAddress.getId() != null && empAddTemp == null) {
            empAddressDtlTempObj = new EmpAddressDtlTemp();
            empAddressDtlTempObj.setIsEditedRecord(true);
        } else if (empAddTemp == null && empAddress.getId() == null) {
            empAddressDtlTempObj = new EmpAddressDtlTemp();
            empAddressDtlTempObj.setIsEditedRecord(false);
        } else {
            empAddressDtlTempObj = empAddTemp;
            empAddressDtlTempObj.setIsEditedRecord(true);
        }

        // ===============================

        if (Objects.equal(empAddress.getAddType(), employeeAddressObj.getAddType())) {
            empAddressDtlTempObj.setAddType(null);
        } else {
            empAddressDtlTempObj.setAddType(employeeAddressObj.getAddType());
        }

        if (Objects.equal(empAddress.getAddressEmp(), employeeAddressObj.getAddressEmp())) {
            empAddressDtlTempObj.setAddressEmp(null);
        } else {
            empAddressDtlTempObj.setAddressEmp(employeeAddressObj.getAddressEmp());
        }

        if (Objects.equal(empAddress.getCountry().getId(), employeeAddressObj.getCountry().getId())) {
            empAddressDtlTempObj.setCountry(null);
        } else {
            empAddressDtlTempObj.setCountry(employeeAddressObj.getCountry());
        }

        if (Objects.equal(empAddress.getState().getId(), employeeAddressObj.getState().getId())) {
            empAddressDtlTempObj.setState(null);
        } else {
            empAddressDtlTempObj.setState(employeeAddressObj.getState());
        }

        if (Objects.equal(empAddress.getDistrict().getId(), employeeAddressObj.getDistrict().getId())) {
            empAddressDtlTempObj.setDistrict(null);
        } else {
            empAddressDtlTempObj.setDistrict(employeeAddressObj.getDistrict());
        }

        if (Objects.equal(empAddress.getCity().getId(), employeeAddressObj.getCity().getId())) {
            empAddressDtlTempObj.setCity(null);
        } else {
            empAddressDtlTempObj.setCity(employeeAddressObj.getCity());
        }

        if (Objects.equal(empAddress.getPincode(), employeeAddressObj.getPincode())) {
            empAddressDtlTempObj.setPincode(null);
        } else {
            empAddressDtlTempObj.setPincode(employeeAddressObj.getPincode());
        }

        if (Objects.equal(empAddress.getAllAddSame(), employeeAddressObj.getAllAddSame())) {
            empAddressDtlTempObj.setAllAddSame(empAddress.getAllAddSame());
        } else {
            if(employeeAddressObj.getAllAddSame() == null || employeeAddressObj.getAllAddSame() == "") {
                empAddressDtlTempObj.setAllAddSame("N");
            }else {
                empAddressDtlTempObj.setAllAddSame("Y");
            }

//			empAddressDtlTempObj.setAllAddSame(employeeAddressObj.getAllAddSame());
        }

        if (Objects.equal(empAddress.getPropertyType(), employeeAddressObj.getPropertyType())) {
            empAddressDtlTempObj.setPropertyType(null);
        } else {
            empAddressDtlTempObj.setPropertyType(employeeAddressObj.getPropertyType());
        }

        empAddressDtlTempObj.setEmp(um.getEmpId());
        if (empAddress.getId() != null) {
            empAddressDtlTempObj.setEmpAddressDtlId(employeeAddressObj.getId());

        }

        empAddressDtlTempObj.setIsDeletedRecord(false);

        if (empAddressDtlTempObj.getId() == null) {

            EmpAddressDtlTemp empAddressTemp = empAddressDtlTempRepository.save(empAddressDtlTempObj);
            if (empAddressTemp != null) {
                EmpAddressDtlTemp empContactObj = (EmpAddressDtlTemp) empAddressTemp;
                res.setObj(empContactObj);
                res.setStatus("SUCCESS");
            }
            auditTrailService.saveAuditTrailData("Employee contact into in temp", "Save", "Admin",
                    NotificationModule.EMPLOYEE_CONTACT_INFO, NotificationAction.ADD, "/save", userId);
        } else {

            EmpAddressDtlTemp empAddressTemp = empAddressDtlTempRepository.save(empAddressDtlTempObj);
            if (empAddressTemp != null) {
                EmpAddressDtlTemp empContactObj = (EmpAddressDtlTemp) empAddressTemp;
                res.setObj(empContactObj);
                res.setStatus("UPDATE");
            }
            auditTrailService.saveAuditTrailData("Employee contact into in temp", "Update", "Admin",
                    NotificationModule.EMPLOYEE_CONTACT_INFO, NotificationAction.UPDATE, "/save", userId);
        }
        return res;
    }

    JsonResponse saveEmpEduInfoTemp(EmpEducationQualificationDtl educationQualificationObj, HttpServletRequest request,
                                    BindingResult result, Model model, Long userId) {

        JsonResponse res = new JsonResponse();

        EmpEducationQualificationDtlTemp empEduQuaTempObj;
        if (educationQualificationObj.getId() != null) {
            empEduQuaTempObj = empEducationQualificationDtlTempRepository.findById(educationQualificationObj.getId())
                    .get();
            empEduQuaTempObj.setUpdatedBy(educationQualificationObj.getCreatedBy());
            empEduQuaTempObj.setUpdatedDate(educationQualificationObj.getCreatedDate());
        } else {
            empEduQuaTempObj = new EmpEducationQualificationDtlTemp();
            empEduQuaTempObj.setCreatedBy(educationQualificationObj.getCreatedBy());
            empEduQuaTempObj.setCreatedDate(educationQualificationObj.getCreatedDate());
        }

        empEduQuaTempObj.setCompany(educationQualificationObj.getCompany());
        empEduQuaTempObj.setCompanyBranch(educationQualificationObj.getCompanyBranch());
        empEduQuaTempObj.setAppId(educationQualificationObj.getAppId());
        empEduQuaTempObj.setIpAddress(educationQualificationObj.getIpAddress());

        if (educationQualificationObj.getQualification() != null) {
            empEduQuaTempObj.setQualification(educationQualificationObj.getQualification());
        }

        if (educationQualificationObj.getModeOfStudy() != null) {
            empEduQuaTempObj.setModeOfStudy(educationQualificationObj.getModeOfStudy());
        }

        if (StringUtil.isNotEmpty(educationQualificationObj.getUniversity())) {
            empEduQuaTempObj.setUniversity(educationQualificationObj.getUniversity());
        }

        if (educationQualificationObj.getPassingOfMonth() != null) {
            empEduQuaTempObj.setPassingOfMonth(educationQualificationObj.getPassingOfMonth());
        }

        if (StringUtil.isNotEmpty(educationQualificationObj.getInstitute())) {
            empEduQuaTempObj.setInstitute(educationQualificationObj.getInstitute());
        }

        if (StringUtil.isNotEmpty(educationQualificationObj.getPassingYear())) {
            empEduQuaTempObj.setPassingYear(educationQualificationObj.getPassingYear());
        }

        if (StringUtil.isNotEmpty(educationQualificationObj.getDuration())) {
            empEduQuaTempObj.setDuration(educationQualificationObj.getDuration());
        }

        if (StringUtil.isNotEmpty(educationQualificationObj.getMajor())) {
            empEduQuaTempObj.setMajor(educationQualificationObj.getMajor());
        }

        if (StringUtil.isNotEmpty(educationQualificationObj.getPercentage())) {
            empEduQuaTempObj.setPercentage(educationQualificationObj.getPercentage());
        }

        if (educationQualificationObj.getGrade() != null) {
            empEduQuaTempObj.setGrade(educationQualificationObj.getGrade());
        }

        if (StringUtil.isNotEmpty(educationQualificationObj.getPercentile())) {
            empEduQuaTempObj.setPercentile(educationQualificationObj.getPercentile());
        }

        if (StringUtil.isNotEmpty(educationQualificationObj.getGpaScore())) {
            empEduQuaTempObj.setGpaScore(educationQualificationObj.getGpaScore());
        }

        if (StringUtil.isNotEmpty(educationQualificationObj.getRemark())) {
            empEduQuaTempObj.setRemark(educationQualificationObj.getRemark());
        }

        if (StringUtil.isNotEmpty(educationQualificationObj.getInstituteAddress())) {
            empEduQuaTempObj.setInstituteAddress(educationQualificationObj.getInstituteAddress());
        }

        empEduQuaTempObj.setEmp(educationQualificationObj.getEmp());

        if (empEduQuaTempObj.getId() == null) {

            EmpEducationQualificationDtlTemp empEducation = empEducationQualificationDtlTempRepository
                    .save(empEduQuaTempObj);
            if (empEducation != null) {
                EmpEducationQualificationDtlTemp educationObj = (EmpEducationQualificationDtlTemp) empEducation;
                res.setObj(educationObj);
                res.setStatus("SUCCESS");
            }
        } else {

            EmpEducationQualificationDtlTemp empEducation = empEducationQualificationDtlTempRepository
                    .save(empEduQuaTempObj);
            if (empEducation != null) {
                EmpEducationQualificationDtlTemp educationObj = (EmpEducationQualificationDtlTemp) empEducation;
                res.setObj(educationObj);
                res.setStatus("UPDATE");
            }
        }
        auditTrailService.saveAuditTrailData("Employee education qualification into temp", "Update", "Admin",
                NotificationModule.EMPLOYEE_EDUCATION_QUALIFICATION_DETAILS, NotificationAction.ADD, "/save", userId);
        return res;
    }

    JsonResponse compareFieldsEmpEduQualInfoTemp(EmpEducationQualificationDtl educationQualificationObj,
                                                 HttpServletRequest request, BindingResult result, Model model, Long userId, HttpSession session)
            throws IllegalAccessException {

        JsonResponse res = new JsonResponse();

        UserMaster um = (UserMaster) session.getAttribute("usermaster");

//		EmpEducationQualificationDtl empEducation = educationQualificationDtlRepository
//				.findByEmployeeId(educationQualificationObj.getEmp().getId());

        EmpEducationQualificationDtl empEducation = null;

        EmpEducationQualificationDtlTemp empEduQualTemp = null;

        if (educationQualificationObj.getId() != null) {
            empEducation = educationQualificationDtlRepository.findById(educationQualificationObj.getId()).get();

            empEduQualTemp = empEducationQualificationDtlTempRepository
                    .findByComIdAndBranchIdAndIsApprovedFalseAndMstId(empEducation.getId(),
                            educationQualificationObj.getCompany().getId(),
                            educationQualificationObj.getCompanyBranch().getId());
        } else {

            empEducation = new EmpEducationQualificationDtl();
        }

        EmpEducationQualificationDtlTemp empEduQualTempObj;
        if (empEduQualTemp == null) {
            empEduQualTempObj = new EmpEducationQualificationDtlTemp();
            empEduQualTempObj.setIsEditedRecord(false);
        } else {
            empEduQualTempObj = empEduQualTemp;
            empEduQualTempObj.setIsEditedRecord(true);
        }

        if (Objects.equal(empEducation.getQualification(), educationQualificationObj.getQualification())) {
            empEduQualTempObj.setQualification(null);
        } else {
            empEduQualTempObj.setQualification(educationQualificationObj.getQualification());
        }

        if (Objects.equal(empEducation.getModeOfStudy(), educationQualificationObj.getModeOfStudy())) {
            empEduQualTempObj.setModeOfStudy(null);
        } else {
            empEduQualTempObj.setModeOfStudy(educationQualificationObj.getModeOfStudy());
        }

        if (Objects.equal(empEducation.getUniversity(), educationQualificationObj.getUniversity())) {
            empEduQualTempObj.setUniversity(null);
        } else {
            empEduQualTempObj.setUniversity(educationQualificationObj.getUniversity());
        }

        if (Objects.equal(empEducation.getPassingOfMonth(), educationQualificationObj.getPassingOfMonth())) {
            empEduQualTempObj.setPassingOfMonth(null);
        } else {
            empEduQualTempObj.setPassingOfMonth(educationQualificationObj.getPassingOfMonth());
        }

        if (Objects.equal(empEducation.getInstitute(), educationQualificationObj.getInstitute())) {
            empEduQualTempObj.setInstitute(null);
        } else {
            empEduQualTempObj.setInstitute(educationQualificationObj.getInstitute());
        }

        if (Objects.equal(empEducation.getPassingYear(), educationQualificationObj.getPassingYear())) {
            empEduQualTempObj.setPassingYear(null);
        } else {
            empEduQualTempObj.setPassingYear(educationQualificationObj.getPassingYear());
        }

        if (Objects.equal(empEducation.getDuration(), educationQualificationObj.getDuration())) {
            empEduQualTempObj.setDuration(null);
        } else {
            empEduQualTempObj.setDuration(educationQualificationObj.getDuration());
        }

        if (Objects.equal(empEducation.getMajor(), educationQualificationObj.getMajor())) {
            empEduQualTempObj.setMajor(null);
        } else {
            empEduQualTempObj.setMajor(educationQualificationObj.getMajor());
        }

        if (Objects.equal(empEducation.getPercentage(), educationQualificationObj.getPercentage())) {
            empEduQualTempObj.setPercentage(null);
        } else {
            empEduQualTempObj.setPercentage(educationQualificationObj.getPercentage());
        }

        if (Objects.equal(empEducation.getGrade(), educationQualificationObj.getGrade())) {
            empEduQualTempObj.setGrade(null);
        } else {
            empEduQualTempObj.setGrade(educationQualificationObj.getGrade());
        }

        if (Objects.equal(empEducation.getPercentile(), educationQualificationObj.getPercentile())) {
            empEduQualTempObj.setPercentile(null);
        } else {
            empEduQualTempObj.setPercentile(educationQualificationObj.getPercentile());
        }

        if (Objects.equal(empEducation.getGpaScore(), educationQualificationObj.getGpaScore())) {
            empEduQualTempObj.setGpaScore(null);
        } else {
            empEduQualTempObj.setGpaScore(educationQualificationObj.getGpaScore());
        }

        if (Objects.equal(empEducation.getRemark(), educationQualificationObj.getRemark())) {
            empEduQualTempObj.setRemark(null);
        } else {
            empEduQualTempObj.setRemark(educationQualificationObj.getRemark());
        }

        if (Objects.equal(empEducation.getInstituteAddress(), educationQualificationObj.getInstituteAddress())) {
            empEduQualTempObj.setInstituteAddress(null);
        } else {
            empEduQualTempObj.setInstituteAddress(educationQualificationObj.getInstituteAddress());
        }

        empEduQualTempObj.setEmp(um.getEmpId());
        if (empEducation.getId() != null) {
            empEduQualTempObj.setEmpEducationQualificationDtlId(educationQualificationObj.getId());
        }

        empEduQualTempObj.setCompany(educationQualificationObj.getCompany());
        empEduQualTempObj.setCompanyBranch(educationQualificationObj.getCompanyBranch());
        empEduQualTempObj.setAppId(educationQualificationObj.getAppId());
        empEduQualTempObj.setIpAddress(educationQualificationObj.getIpAddress());

        empEduQualTempObj.setIsDeletedRecord(false);
        empEduQualTempObj.setIsEditedRecord(true);

        if (empEduQualTempObj.getId() == null) {

            empEduQualTempObj.setCreatedBy(educationQualificationObj.getCreatedBy());
            empEduQualTempObj.setCreatedDate(educationQualificationObj.getCreatedDate());

            EmpEducationQualificationDtlTemp empEducationTemp = empEducationQualificationDtlTempRepository
                    .save(empEduQualTempObj);
            if (empEducationTemp != null) {
                EmpEducationQualificationDtlTemp empEduQualDtlTemp = (EmpEducationQualificationDtlTemp) empEducationTemp;
                res.setObj(empEduQualDtlTemp);
                res.setStatus("UPDATE");
            }
        } else {

            empEduQualTempObj.setUpdatedBy(educationQualificationObj.getCreatedBy());
            empEduQualTempObj.setUpdatedDate(educationQualificationObj.getCreatedDate());

            EmpEducationQualificationDtlTemp empEducationTemp = empEducationQualificationDtlTempRepository
                    .save(empEduQualTempObj);
            if (empEducationTemp != null) {
                EmpEducationQualificationDtlTemp empEduQualDtlTemp = (EmpEducationQualificationDtlTemp) empEducationTemp;
                res.setObj(empEduQualDtlTemp);
                res.setStatus("UPDATE");
            }
        }
        auditTrailService.saveAuditTrailData("Employee education qualification in temp", "Save", "Admin",
                NotificationModule.EMPLOYEE_EDUCATION_QUALIFICATION_DETAILS, NotificationAction.ADD, "/save", userId);
        return res;
    }

    @GetMapping(value = "/forCheckingEmployeeAssignWorkflowOrNot")
    @ResponseBody
    public Boolean forCheckingEmployeeAssignWorkflowOrNot(HttpServletRequest request, Model model,
                                                          HttpServletResponse response, HttpSession session, @RequestParam("employeeId") Long employeeId) {
        try {

            // UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            WorkflowRuleConfigurationMaster ruleConfigurationMaster = configuratiionMasterRepository
                    .findByWorkflowTypeAndEmployeeIdToGetLatestEmployeeRule(companyId, companyBranchId,
                            WorkflowType.EMPLOYEE_APPLICATION.getTypeId(),
                            userMasterRepository.findByEmpId(employeeId).getId().toString());
            if (ruleConfigurationMaster != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @GetMapping(value = "/generateWorkFlow")
    @ResponseBody
    public Boolean generateWorkFlow(HttpServletRequest request, Model model,
                                    HttpServletResponse response, HttpSession session, @RequestParam("employeeId") Long employeeId) {
        try {

            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");
            Long roleId = (Long) session.getAttribute("roleId");
            RoleMaster roleMaster = roleMasterRepository.findByIdAndIsDelete(roleId, false);

            Employee emp = employeeRepository.findById(employeeId).get();

            // Code for Workflow State machine

            EmployeeAppWFInstanceDto Wdto = new EmployeeAppWFInstanceDto();

            Wdto.setTypeId(WorkflowType.EMPLOYEE_APPLICATION);
            Wdto.setWorkflowVersion((short) 1);
            Wdto.setCreateDate(LocalDateTime.now());
            Wdto.setCompanyId(companyId);
            Wdto.setBranchId(Integer.parseInt(companyBranchId.toString()));
            Wdto.setCreatedByUserId(um.getId());

            Wdto.setEmployeeApplication(emp);

            UserMaster userMaster = userMasterRepository.findByEmpId(emp.getId());

            WorkflowRuleConfigurationMaster ruleConfigurationMaster = configuratiionMasterRepository
                    .findByWorkflowTypeAndEmployeeIdToGetLatestEmployeeRule(companyId, companyBranchId,
                            Wdto.getTypeId().getTypeId(), userMaster.getId().toString());
            /* if rule found then need to use that otherwise default rule applied */
            if (ruleConfigurationMaster != null) {
                Wdto.setRuleConfigurationMaster(ruleConfigurationMaster);
            } else {
                Wdto.setRuleConfigurationMaster(
                        configuratiionMasterRepository.findAllByRuleNameIgnoreCase("DEFAULT_RULE").get(0));
            }

            /* for setting reviewers to application for defined workflow type */
            /* List<Pair<Integer, Set<Long>>> */
            Wdto.setReviewers(getEmployeeReviwerListForWorkFlowDto(emp, Wdto.getRuleConfigurationMaster(), session));

            System.err.println("=========" + Wdto);

            // Call to create Application WorkFlow Service
            EmployeeAppWorkFlowInstanceEntity employeeAppWorkFlowInstanceEntity;

            if (roleMaster.getIsAdmin()) {
                employeeAppWorkFlowInstanceEntity = employeeAppService
                        .createByAdmin(EmployeeAppWFInstanceDto.toEntity(Wdto));

                // auto approved when admin create application for employee.
                emp.setAprvId(um);
                emp.setApproverStatus("APPROVED");
            } else {
                employeeAppWorkFlowInstanceEntity = employeeAppService.create(EmployeeAppWFInstanceDto.toEntity(Wdto));
            }

            /* Save workflow employee instance entity into the employee main table */

            emp.setEmployeeAppWorkflow(employeeAppWorkFlowInstanceEntity.getId());

            /* Save after everything set into the object */
            Employee em = employeeRepository.save(emp);

            System.err.println("Employee WorkFlow Entity created Successfully");

            /* For sending the notifications */
            if(employeeAppWorkFlowInstanceEntity.getId()!=null) {
                sendNotification(userMasterRepository.findByEmpId(em.getId()), companyId, companyBranchId,
                        employeeAppWorkFlowInstanceEntity, em);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @ResponseBody
    @GetMapping("/export-to-excel")
    public void exportIntoExcelFile(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.err.println("Enter Export to Excel");
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=employeeStaticData.xlsx";
        response.setHeader(headerKey, headerValue);

        try {
            // Use the class loader to get the resource as a stream
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("employeeStaticData.xlsx");

            if (inputStream == null) {
                throw new IOException("File not found: employeeStaticData.xlsx");
            }

            // Read the file content and write it to the response output stream
            try (OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Reset the buffer position to 0
                outputStream.flush();
            }

            System.err.println("File downloaded successfully.");

        } catch (IOException e) {
            System.err.println("Error downloading file: " + e.getMessage());
        }
    }

    // Function to format empCodePostfix to ensure it is 6 digits
    private String formatEmpCodePostfix(String empCodePostfix) {
        // Check if the length is less than 6
        if (empCodePostfix.length() < 6) {
            // Append zeros to the front to make it 6 digits
            return "0".repeat(6 - empCodePostfix.length()) + empCodePostfix;
        }
        return empCodePostfix;
    }

    @PostMapping("/uploadFile")
    public @ResponseBody AjaxResponseBody uploadFile(@RequestPart("excelFile") MultipartFile files,
                                                     RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");
            LocalDate currentDate = LocalDate.now();
            java.sql.Date currentSqlDate = java.sql.Date.valueOf(currentDate);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Adjust the format as needed
            String dateString = sdf.format(currentSqlDate);

            XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            List<List<String>> excelData = new ArrayList<>();

            List<EmployeeExcelDto> EmployeeExcelDto = new ArrayList<>();

            // Create a data formatter
            DataFormatter dataFormatter = new DataFormatter();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

            // Loop through rows (skip header)
            //for 0th sheet
            for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet.getRow(rowIndex);


                List<String> rowData = new ArrayList<>();
                EmployeeExcelDto employeeExcelDto = new EmployeeExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex <= row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);



                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData.add(formattedDate);
                    } else {
                        rowData.add(formattedValue);
                    }

                    // Print the cell value
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData.get(cellIndex));
                }

                excelData.add(rowData);
                CompanyMaster companyMasterCodeList = compRepo.findById(companyId).orElse(null);

                employeeExcelDto.setEmp_code_prefix(companyMasterCodeList.getCompanyCodePrefix());

                employeeExcelDto.setEmp_code_postfix(rowData.get(0));
                employeeExcelDto.setPan_number(rowData.get(1));
                employeeExcelDto.setOld_emp_code(rowData.get(2));
                employeeExcelDto.setDate_of_appointment(rowData.get(3));
                employeeExcelDto.setBio_metric_id(rowData.get(4));
                employeeExcelDto.setSalutation(rowData.get(5));

                if (rowData.get(6) != null && !rowData.get(6).isEmpty()) {
                    List<DepartmentMaster> dptm = departmentMasterRepository.findAllByIsDeleteFalseAndDepartmentMasterName(rowData.get(6), companyId, companyBranchId);

                    if (!dptm.isEmpty()) {
                        employeeExcelDto.setDepartment(String.valueOf(dptm.get(0).getId()));
                    } else {
                        // Handle the case when the list is empty, set to "invalid" or any other appropriate value.
                        employeeExcelDto.setDepartment("invalid");
                    }
                } else {
                    employeeExcelDto.setDepartment(null);
                }
                employeeExcelDto.setFirst_name(rowData.get(7));
                employeeExcelDto.setMiddle_name(rowData.get(8));
                employeeExcelDto.setLast_name(rowData.get(9));
//	            employeeExcelDto.setUnit(unitList.get(0).getCode());
                if (rowData.get(10) != null && !rowData.get(10).isEmpty()) {
                    List<HrmsCode> unitList = hrmsCodeService.findByDescription(rowData.get(10));
                    if(!unitList.isEmpty()) {
                        employeeExcelDto.setUnit(unitList.get(0).getCode());
                    }
                    else {
                        employeeExcelDto.setUnit("invalid");
                    }
                } else {
                    employeeExcelDto.setUnit(null);
                }

                if (rowData.get(11) != null && !rowData.get(11).isEmpty()) {
                    List<DesignationMaster> dm = designationMasterRepository.findAllByIsDeleteFalseAndDesignationName(rowData.get(11),companyId,companyBranchId);
                    if(!dm.isEmpty()) {
                        employeeExcelDto.setDesignation(String.valueOf(dm.get(0).getId()));
                    }
                    else {
                        employeeExcelDto.setDesignation("invalid");
                    }
                } else {
                    employeeExcelDto.setDesignation(null);
                }
                if (rowData.get(12) != null && !rowData.get(12).isEmpty()) {
                    List<GroupMaster> gm= groupMasterRepository.findAllByIsDeleteFalseAndGroupName(rowData.get(12),companyId,companyBranchId);
                    if(!gm.isEmpty()) {
                        employeeExcelDto.setGroup(String.valueOf(gm.get(0).getId()));
                    }
                    else {
                        employeeExcelDto.setGroup("invalid");
                    }
                } else {
                    employeeExcelDto.setGroup(null);
                }
                employeeExcelDto.setGazeted(Boolean.parseBoolean(rowData.get(13)));
                employeeExcelDto.setEmployee_eligible_for(rowData.get(14));
                employeeExcelDto.setEmp_under_gratuity_act(rowData.get(15));
                employeeExcelDto.setCreated_by(um.getId());
                employeeExcelDto.setApp_id(1L);
                employeeExcelDto.setCom(companyId);
                employeeExcelDto.setBranch(companyBranchId);
                employeeExcelDto.setUpdated_by(um.getId());
                employeeExcelDto.setCreated_date(dateString);
                employeeExcelDto.setUpdated_date(dateString);

//	            if (StringUtil.isNotEmpty(employeeExcelDto.getEmp_code_prefix())) {
//	            	Optional<CompanyMaster> cm = companyMasterRepository.findById(companyId);
//				    if (!cm.get().getCompanyShortName().toLowerCase().contains("irfcl")) {
//				        // For companies other than "irfcl"
//				    	employeeExcelDto.setEmp_code(employeeExcelDto.getEmp_code_prefix() + "/" + employeeExcelDto.getEmp_code_postfix());
//				    } else {
//				        // For "irfcl" company
//				    	employeeExcelDto.setEmp_code(employeeExcelDto.getEmp_code_prefix() + "/" + employeeExcelDto.getEmp_code_postfix());
//				    }
//				} else {
//				    // If empCodePrefix is empty, set empCodePostfix as empCode
//					employeeExcelDto.setEmp_code(employeeExcelDto.getEmp_code_postfix());
//				}
                if (StringUtil.isNotEmpty(employeeExcelDto.getEmp_code_prefix())) {
                    Optional<CompanyMaster> cm = companyMasterRepository.findById(companyId);
                    if (!cm.get().getCompanyShortName().toLowerCase().contains("irfcl")) {
                        // For companies other than "irfcl"
                        employeeExcelDto.setEmp_code(employeeExcelDto.getEmp_code_prefix() + "/" + formatEmpCodePostfix(employeeExcelDto.getEmp_code_postfix()));
                    } else {
                        // For "irfcl" company
                        employeeExcelDto.setEmp_code(employeeExcelDto.getEmp_code_prefix() + "/" + formatEmpCodePostfix(employeeExcelDto.getEmp_code_postfix()));
                    }
                } else {
                    // If empCodePrefix is empty, set empCodePostfix as empCode
                    employeeExcelDto.setEmp_code(formatEmpCodePostfix(employeeExcelDto.getEmp_code_postfix()));
                }

                EmployeeExcelDto.add(employeeExcelDto);
            }
            System.out.println("Data in Employee Excel Dto"+EmployeeExcelDto);

            //for 1st sheet
            XSSFSheet worksheet1 = workbook.getSheetAt(1);

            List<List<String>> excelData1 = new ArrayList<>();
            List<PersonalExcelDto> PersonalExcelDto = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= worksheet1.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet1.getRow(rowIndex);

                List<String> rowData1 = new ArrayList<>();
                PersonalExcelDto personalExcelDto = new PersonalExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex <= row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData1.add(formattedDate);
                    } else {
                        rowData1.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData1.get(cellIndex));
                }
                excelData1.add(rowData1);

                //List<ReligionMaster> rm = religionMasterRepository.findAllByIsDeleteFalseAndReligionName(rowData1.get(8),companyId,companyBranchId);
                //System.out.println(rm);

//	            List<CastMaster> cm = castMasterRepository
//	            		.findAllByIsDeleteFalseAndCastName(rowData1.get(9),companyId,companyBranchId);
//	            System.out.println(cm);

//	            List<CountryMaster> countryM = countryMasterRepository.findAllBycountryname(rowData1.get(11));
//	            System.out.println(countryM);

//	            List<BankMaster> bm= bankMasterRepository.findAllByIsDeleteFalseAndBankName(rowData1.get(14),companyId,companyBranchId);
//	            System.out.println(bm);

//	            BankBranchMaster bbm = bankBranchMasterRepository.findByIsDeleteFalseAndBranchNameAndCompanyIdAndCompanyBranchId(rowData1.get(15), companyId, companyBranchId);
//	            System.out.println(bbm);

//	            List<StateMaster> sm = stateMasterRepositry.findAllByIsDeleteFalseAndStateName(rowData1.get(25));
//	            System.out.println(sm);

//	            personalExcelDto.setGender(rowData1.get(0));
                if (rowData1.get(0) != null) {
                    switch (rowData1.get(0).toUpperCase()) {
                        case "MALE":
                            personalExcelDto.setGender("M");
                            break;
                        case "FEMALE":
                            personalExcelDto.setGender("F");
                            break;
                        case "TRANS GENDER":
                            personalExcelDto.setGender("T");
                            break;
                        default:
                            personalExcelDto.setGender("M");
                            break;
                    }
                }
//	            personalExcelDto.setMarriage_status(rowData1.get(1));
                if (rowData1.get(1) != null) {
                    switch (rowData1.get(1).toUpperCase()) {
                        case "MARRIED":
                            personalExcelDto.setMarriage_status("M");
                            break;
                        case "UNMARRIED":
                            personalExcelDto.setMarriage_status("U");
                            break;
                        case "DIVORCED":
                            personalExcelDto.setMarriage_status("D");
                            break;
                        case "WIDOW":
                            personalExcelDto.setMarriage_status("W");
                            break;
                        case "WIDOWER":
                            personalExcelDto.setMarriage_status("Wr");
                            break;
                        default:
                            // Handle any other values as needed
                            break;
                    }
                }
                personalExcelDto.setDate_of_birth(rowData1.get(2));
                personalExcelDto.setDate_of_marriage(rowData1.get(3));
                personalExcelDto.setBirth_place(rowData1.get(4));
//	            personalExcelDto.setCategory(rowData1.get(5));
                if (rowData1.get(5) != null) {
                    switch (rowData1.get(5).toUpperCase()) {
                        case "GENERAL":
                            personalExcelDto.setCategory("OP");
                            break;
                        case "SEBC":
                            personalExcelDto.setCategory("SEBC");
                            break;
                        case "SC":
                            personalExcelDto.setCategory("SC");
                            break;
                        case "ST":
                            personalExcelDto.setCategory("ST");
                            break;
                        default:
                            // Handle any other values as needed
                            break;
                    }
                }
                personalExcelDto.setUid_no(rowData1.get(6));
                personalExcelDto.setGpf_no(rowData1.get(7));
                // personalExcelDto.setReligion_id(rowData1.get(8));

                if (rowData1.get(8) != null && !rowData1.get(8).isEmpty()) {
                    List<ReligionMaster> rm = religionMasterRepository.findAllByIsDeleteFalseAndReligionName(rowData1.get(8),companyId,companyBranchId);
                    if(!rm.isEmpty()) {
                        personalExcelDto.setReligion_id(String.valueOf(rm.get(0).getId()));
                    }
                    else {
                        personalExcelDto.setReligion_id("invalid");
                    }
                }
                else {
                    personalExcelDto.setReligion_id(null);
                }
                // personalExcelDto.setCast_id(rowData1.get(9));
                if (rowData1.get(9) != null && !rowData1.get(9).isEmpty()) {
                    List<CastMaster> cm = castMasterRepository.findAllByIsDeleteFalseAndCastName(rowData1.get(9),companyId,companyBranchId);
                    if(!cm.isEmpty()) {
                        personalExcelDto.setCast_id(String.valueOf(cm.get(0).getId()));
                    }
                    else {
                        personalExcelDto.setCast_id("invalid");
                    }
                }
                else {
                    personalExcelDto.setCast_id(null);
                }
                personalExcelDto.setComm_category_ref(rowData1.get(10));
                // personalExcelDto.setCountry_id(rowData1.get(11));
                if (rowData1.get(11) != null && !rowData1.get(11).isEmpty()) {
                    List<CountryMaster> countryM = countryMasterRepository.findAllBycountryname(rowData1.get(11));
                    if(!countryM.isEmpty()) {
                        personalExcelDto.setCountry_id(String.valueOf(countryM.get(0).getId()));
                    }
                    else {
                        personalExcelDto.setCountry_id("invalid");
                    }
                }
                else {
                    personalExcelDto.setCountry_id(null);
                }
                personalExcelDto.setPli_no(rowData1.get(12));
                personalExcelDto.setHobbies(rowData1.get(13));
                // personalExcelDto.setBank_id(rowData1.get(14));
                if (rowData1.get(14) != null && !rowData1.get(14).isEmpty()) {
                    List<BankMaster> bm= bankMasterRepository.findAllByIsDeleteFalseAndBankName(rowData1.get(14),companyId,companyBranchId);
                    if(!bm.isEmpty()) {
                        personalExcelDto.setBank_id(String.valueOf(bm.get(0).getId()));
                    }
                    else {
                        personalExcelDto.setBank_id("invalid");
                    }
                }
                else {
                    personalExcelDto.setBank_id(null);
                }
                // personalExcelDto.setBank_branch_id(rowData1.get(15));
                if (rowData1.get(15) != null ) {
                    BankBranchMaster bbm = bankBranchMasterRepository.findByIsDeleteFalseAndBranchNameAndCompanyIdAndCompanyBranchId(rowData1.get(15), companyId, companyBranchId);
                    if(bbm!=null) {
                        personalExcelDto.setBank_branch_id(String.valueOf(bbm.getId()));
                    }
                    else {
                        personalExcelDto.setBank_branch_id("invalid");
                    }
                }
                else {
                    personalExcelDto.setBank_branch_id(null);
                }
                personalExcelDto.setBank_acc_no(rowData1.get(16));
                personalExcelDto.setIfsc_code(rowData1.get(17));
                personalExcelDto.setBsr_code(rowData1.get(18));
                personalExcelDto.setPassport_no(rowData1.get(19));
                personalExcelDto.setDate_of_expiry(rowData1.get(20));
                personalExcelDto.setVisa_detail(rowData1.get(21));
                personalExcelDto.setDriving_license_no(rowData1.get(22));
                personalExcelDto.setLicense_issued_for(rowData1.get(23));
                personalExcelDto.setValid_upto(rowData1.get(24));
                // personalExcelDto.setState_id(rowData1.get(25));
                if (rowData1.get(25) != null && !rowData1.get(25).isEmpty()) {
                    List<StateMaster> sm = stateMasterRepositry.findAllByIsDeleteFalseAndStateName(rowData1.get(25));
                    if(!sm.isEmpty()) {
                        personalExcelDto.setState_id(String.valueOf(sm.get(0).getId()));
                    }
                    else {
                        personalExcelDto.setState_id("invalid");
                    }
                }
                else {
                    personalExcelDto.setState_id(null);
                }
                personalExcelDto.setGovt_vehicle_provided(rowData1.get(26));
                personalExcelDto.setIs_resident_other_country(rowData1.get(27));
                personalExcelDto.setAddi_info(rowData1.get(29));
                personalExcelDto.setIs_any_disciplinary_proceedigns(rowData1.get(28));
                personalExcelDto.setEmployee_code(rowData1.get(30));
                PersonalExcelDto.add(personalExcelDto);
            }
            System.out.println("Data in personal Excel Dto"+PersonalExcelDto);

            //for 2nd sheet
            XSSFSheet worksheet2 = workbook.getSheetAt(2);

            List<List<String>> excelData2 = new ArrayList<>();
            List<ContactExcelDto> ContactExcelDto = new ArrayList<>();

            for (int rowIndex = 1; rowIndex <= worksheet2.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet2.getRow(rowIndex);

                List<String> rowData2 = new ArrayList<>();
                ContactExcelDto contactExcelDto = new ContactExcelDto();

                // Loop through cells in the row

                for (int cellIndex = 0; cellIndex <= row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData2.add(formattedDate);
                    } else {
                        rowData2.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData2.get(cellIndex));
                }
                excelData2.add(rowData2);

                contactExcelDto.setWork_phone(rowData2.get(0));
                contactExcelDto.setExtension(rowData2.get(1));
                contactExcelDto.setWork_mobile(rowData2.get(2));
                contactExcelDto.setHome_phone(rowData2.get(3));
                contactExcelDto.setHome_mobile(rowData2.get(4));
                contactExcelDto.setCorporate_email(rowData2.get(5));
                contactExcelDto.setPersonal_email(rowData2.get(6));
//	            contactExcelDto.setSecondary_email(rowData2.get(7));
                contactExcelDto.setEmployee_code(rowData2.get(8));
                ContactExcelDto.add(contactExcelDto);
            }
            System.out.println("Data in Contact Excel Dto"+ContactExcelDto);


            //for 3rd sheet
            XSSFSheet worksheet3 = workbook.getSheetAt(3);

            List<List<String>> excelData3 = new ArrayList<>();
            List<AddressExcelDto> AddressExcelDto = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= worksheet3.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet3.getRow(rowIndex);

                List<String> rowData3 = new ArrayList<>();
                AddressExcelDto addressExcelDto = new AddressExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex <= row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData3.add(formattedDate);
                    } else {
                        rowData3.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData3.get(cellIndex));
                }
                excelData3.add(rowData3);
                String addressTypeValue = rowData3.get(0);
                String addressType;

                if ("Permanent".equalsIgnoreCase(addressTypeValue)) {
                    addressType = "PRM";
                } else if ("Correspondence".equalsIgnoreCase(addressTypeValue)) {
                    addressType = "CRS";
                } else if ("Resident".equalsIgnoreCase(addressTypeValue)) {
                    addressType = "RES";
                } else {
                    // Handle other cases or set a default value if needed
                    addressType = "UNKNOWN";
                }

                addressExcelDto.setAddress_type(addressType);
                addressExcelDto.setAddress(rowData3.get(1));
//	            List<CountryMaster> cm = countryMasterRepository.findAllBycountryname(rowData3.get(2));
                if (rowData3.get(2) != null || !rowData3.get(2).isEmpty()) {
                    List<CountryMaster> cm = countryMasterRepository.findAllBycountryname(rowData3.get(2));
                    if(!cm.isEmpty()) {
                        addressExcelDto.setCountry(String.valueOf(cm.get(0).getId()));
                    }
                    else {
                        addressExcelDto.setCountry("invalid");
                    }
                }
                else {
                    addressExcelDto.setCountry(null);
                }

//	            List<StateMaster> sm = stateMasterRepositry.findStateByName(rowData3.get(3));
                if (rowData3.get(3) != null || !rowData3.get(3).isEmpty()) {
                    List<StateMaster> sm = stateMasterRepositry.findStateByName(rowData3.get(3));
                    if(!sm.isEmpty()) {
                        addressExcelDto.setState(String.valueOf(sm.get(0).getId()));
                    }
                    else {
                        addressExcelDto.setState("invalid");
                    }
                }
                else {
                    addressExcelDto.setState(null);
                }
//	            List<DistrictMaster> dm= districtMasterRepository.finfByName(rowData3.get(4));
                if (rowData3.get(4) != null || !rowData3.get(4).isEmpty()) {
                    List<DistrictMaster> dm= districtMasterRepository.finfByName(rowData3.get(4));
                    if(!dm.isEmpty()) {
                        addressExcelDto.setDistrict(String.valueOf(dm.get(0).getId()));
                    }
                    else {
                        addressExcelDto.setDistrict("invalid");
                    }
                }
                else {
                    addressExcelDto.setDistrict(null);
                }
//	            CityMaster city = cityMasterRepository.findCityByName(rowData3.get(5));
                if (rowData3.get(5) != null || !rowData3.get(5).isEmpty()) {
                    CityMaster city = cityMasterRepository.findCityByName(rowData3.get(5));
                    if(city!=null) {
                        addressExcelDto.setCity(String.valueOf(city.getId()));
                    }else {
                        addressExcelDto.setCity("invalid");
                    }
                }
                else {
                    addressExcelDto.setCity(null);
                }
                addressExcelDto.setPincode(rowData3.get(6));
                addressExcelDto.setAll_address_same(Boolean.parseBoolean(rowData3.get(7)));
                addressExcelDto.setProperty_type(rowData3.get(8));
                addressExcelDto.setEmployee_code(rowData3.get(9));
                AddressExcelDto.add(addressExcelDto);
            }
            System.out.println("Items in address excel dto "+AddressExcelDto);


            //for 4th sheet
            XSSFSheet worksheet4 = workbook.getSheetAt(4);

            List<List<String>> excelData4 = new ArrayList<>();
            List<FamilyExcelDto> FamilyExcelDto = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= worksheet4.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet4.getRow(rowIndex);

                List<String> rowData4 = new ArrayList<>();
                FamilyExcelDto familyExcelDto = new FamilyExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex <= row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData4.add(formattedDate);
                    } else {
                        rowData4.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData4.get(cellIndex));
                }
                excelData4.add(rowData4);
                familyExcelDto.setFirst_name(rowData4.get(0));
                familyExcelDto.setMiddle_name(rowData4.get(1));
                familyExcelDto.setLast_name(rowData4.get(2));



                if (rowData4.get(3) != null && !rowData4.get(3).isEmpty()) {
                    List<FamilyRelationMaster> rm = familyRelationMasterRepository.findAllByIsDeleteFalseAndFamilyRlationName(rowData4.get(3), companyId, companyBranchId);
                    //System.out.println("hello i am inside relation master repo "+rm);
                    if(!rm.isEmpty()) {
                        familyExcelDto.setRelation(String.valueOf(rm.get(0).getId()));
                    }
                    else {
                        familyExcelDto.setRelation("invalid");
                    }
                }
                else {
                    familyExcelDto.setRelation(null);
                }
                familyExcelDto.setDate_of_birth(rowData4.get(4));
//	            familyExcelDto.setGender(rowData4.get(5));
                if (rowData4.get(5) != null) {
                    switch (rowData4.get(5).toUpperCase()) {
                        case "MALE":
                            familyExcelDto.setGender("M");
                            break;
                        case "FEMALE":
                            familyExcelDto.setGender("F");
                            break;
                        case "TRANS GENDER":
                            familyExcelDto.setGender("T");
                            break;
                        default:
                            familyExcelDto.setGender(null);
                            break;
                    }
                }
                familyExcelDto.setNominee(Boolean.parseBoolean(rowData4.get(6)));
//	            familyExcelDto.setMarital_status(rowData4.get(7));
                if (rowData4.get(7) != null) {
                    switch (rowData4.get(7).toUpperCase()) {
                        case "MARRIED":
                            familyExcelDto.setMarital_status("M");
                            break;
                        case "UNMARRIED":
                            familyExcelDto.setMarital_status("U");
                            break;
                        case "DIVORCED":
                            familyExcelDto.setMarital_status("D");
                            break;
                        case "WIDOW":
                            familyExcelDto.setMarital_status("W");
                            break;
                        case "WIDOWER":
                            familyExcelDto.setMarital_status("Wr");
                            break;
                        default:
                            // Handle any other values as needed
                            break;
                    }
                }
                familyExcelDto.setOccupation(rowData4.get(8));
                familyExcelDto.setDependent(Boolean.parseBoolean(rowData4.get(9)));
                familyExcelDto.setContact_detail(rowData4.get(10));


                if (rowData4.get(11) != null && !rowData4.get(11).isEmpty()) {
                    List<CountryMaster> cm = countryMasterRepository.findAllBycountryname(rowData4.get(11));
                    if(!cm.isEmpty()) {
                        familyExcelDto.setNationality(String.valueOf(cm.get(0).getId()));
                    }
                    else {
                        familyExcelDto.setNationality("invalid");
                    }
                }
                else {
                    familyExcelDto.setNationality(null);
                }
                familyExcelDto.setResiding_with(Boolean.parseBoolean(rowData4.get(12)));
                familyExcelDto.setCan_be_contacted_in_emergency(Boolean.parseBoolean(rowData4.get(13)));
                familyExcelDto.setPhysically_disabled(Boolean.parseBoolean(rowData4.get(14)));
                familyExcelDto.setAddress(rowData4.get(15));
                familyExcelDto.setEmployee_code(rowData4.get(16));
                familyExcelDto.setCom_id(companyId);
                familyExcelDto.setBranch_id(companyBranchId);
                familyExcelDto.setCreated_by(um.getId());
                familyExcelDto.setCreated_date(dateString);
                familyExcelDto.setUpdated_date(dateString);
                FamilyExcelDto.add(familyExcelDto);
            }
            System.out.println("Data in family Excel Dto"+FamilyExcelDto);

            //Fetch 5th sheet
            XSSFSheet worksheet5 = workbook.getSheetAt(5);

            List<List<String>> excelData5 = new ArrayList<>();
            List<EmergencyExcelDto> EmergencyExcelDto = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= worksheet5.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet5.getRow(rowIndex);

                List<String> rowData5 = new ArrayList<>();
                EmergencyExcelDto emergencyExcelDto = new EmergencyExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex <= row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData5.add(formattedDate);
                    } else {
                        rowData5.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData5.get(cellIndex));
                }
                excelData5.add(rowData5);
                emergencyExcelDto.setPriority(rowData5.get(0));
                emergencyExcelDto.setFirst_name(rowData5.get(1));
                emergencyExcelDto.setMiddle_name(rowData5.get(2));
                emergencyExcelDto.setLast_name(rowData5.get(3));


//	            if (rowData5.get(4) != null && !rowData5.get(4).isEmpty()) {
//	            	List<FamilyRelationMaster> rm=familyRelationMasterRepository.findAllByIsDeleteFalseAndFamilyRlationName(rowData5.get(4), companyId, companyBranchId);
//	            	emergencyExcelDto.setRelation(rm.get(0).getId());
//	            }
//	            else {
//	            	emergencyExcelDto.setRelation(null);
//	            }
                if (rowData5.get(4) != null && !rowData5.get(4).isEmpty()) {
                    List<FamilyRelationMaster> rm=familyRelationMasterRepository.findAllByIsDeleteFalseAndFamilyRlationName(rowData5.get(4), companyId, companyBranchId);
                    if (rm != null && !rm.isEmpty()) {
                        emergencyExcelDto.setRelation(String.valueOf(rm.get(0).getId()));
                    }
                    else {
                        emergencyExcelDto.setRelation("invalid");
                    }
                }
                else {
                    emergencyExcelDto.setRelation(null);
                }
                emergencyExcelDto.setPhone_no(rowData5.get(5));
                emergencyExcelDto.setMobile_no(rowData5.get(6));
                emergencyExcelDto.setEmail(rowData5.get(7));
                emergencyExcelDto.setAddress(rowData5.get(8));
                emergencyExcelDto.setEmployee_code(rowData5.get(9));
                EmergencyExcelDto.add(emergencyExcelDto);
            }
            System.out.println("data in emergency excel dto"+EmergencyExcelDto);

            //Fetch 6th sheet
            XSSFSheet worksheet6 = workbook.getSheetAt(6);

            List<List<String>> excelData6 = new ArrayList<>();
            List<NomineeExcelDto> NomineeExcelDto = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= worksheet6.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet6.getRow(rowIndex);

                List<String> rowData6 = new ArrayList<>();
                NomineeExcelDto nomineeExcelDto = new NomineeExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex <= row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData6.add(formattedDate);
                    } else {
                        rowData6.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData6.get(cellIndex));
                }
                excelData6.add(rowData6);
                nomineeExcelDto.setPriority(rowData6.get(0));
                nomineeExcelDto.setFirst_name(rowData6.get(1));
                nomineeExcelDto.setMiddle_name(rowData6.get(2));
                nomineeExcelDto.setLast_name(rowData6.get(3));


                if (rowData6.get(4) != null && !rowData6.get(4).isEmpty()) {
                    List<FamilyRelationMaster> rm=familyRelationMasterRepository.findAllByIsDeleteFalseAndFamilyRlationName(rowData6.get(4), companyId, companyBranchId);
                    if(!rm.isEmpty()) {
                        nomineeExcelDto.setRelation(String.valueOf(rm.get(0).getId()));
                    }
                    else {
                        nomineeExcelDto.setRelation("invalid");
                    }
                }
                else {
                    nomineeExcelDto.setRelation(null);
                }
//	            nomineeExcelDto.setGender(rowData6.get(5));
                if (rowData6.get(5) != null) {
                    switch (rowData6.get(5).toUpperCase()) {
                        case "MALE":
                            nomineeExcelDto.setGender("M");
                            break;
                        case "FEMALE":
                            nomineeExcelDto.setGender("F");
                            break;
                        case "TRANS GENDER":
                            nomineeExcelDto.setGender("T");
                            break;
                        default:
                            nomineeExcelDto.setGender(null);
                            break;
                    }
                }
                nomineeExcelDto.setDate_of_birth(rowData6.get(6));
                nomineeExcelDto.setUid_no(rowData6.get(7));
                nomineeExcelDto.setPan_no(rowData6.get(8));
                nomineeExcelDto.setContact_no(rowData6.get(9));
                nomineeExcelDto.setNominee_invalid_condition(rowData6.get(10));
                nomineeExcelDto.setEmployee_code(rowData6.get(11));
                nomineeExcelDto.setCom_id(companyId);
                nomineeExcelDto.setBranch_id(companyBranchId);

                NomineeExcelDto.add(nomineeExcelDto);
            }
            System.out.println("inside nominee excel dto"+NomineeExcelDto);

            //for 9th sheet
            XSSFSheet worksheet9 = workbook.getSheetAt(9);

            List<List<String>> excelData9 = new ArrayList<>();
            List<JobExcelDto> JobExcelDto = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= worksheet9.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet9.getRow(rowIndex);

                List<String> rowData9 = new ArrayList<>();
                JobExcelDto jobExcelDto = new JobExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData9.add(formattedDate);
                    } else {
                        rowData9.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData9.get(cellIndex));
                }
                excelData9.add(rowData9);

                if (rowData9.get(0) != null && !rowData9.get(0).trim().isEmpty()) {
                    List<EmployeementType> employeementTypeList = empTypeService.findALlByIsDeleteFalse();
                    System.out.println(employeementTypeList);
                    String employeementTypeName = rowData9.get(0).trim();
                    Long employeementTypeId = null;
                    boolean foundMatch = false;

                    for (EmployeementType employeementType : employeementTypeList) {
                        if (employeementTypeName.equals(employeementType.getEmployeementTypeName())) {
                            employeementTypeId = employeementType.getId();
                            foundMatch = true;
                            break;  // exit the loop once a match is found
                        }
                    }

                    if (!foundMatch) {
                        jobExcelDto.setEmployement_type_id("invalid");
                    } else {
                        jobExcelDto.setEmployement_type_id(String.valueOf(employeementTypeId));
                    }
                } else {
                    jobExcelDto.setEmployement_type_id(null);
                }


                if(rowData9.get(1)!=null && !rowData9.get(1).isEmpty()) {
                    List<EmployeementCategory> empCatList = empCatService.findAllByIsDeleteFalse();
                    System.out.println(empCatList);
                    String empCatName=rowData9.get(1);
                    Long empCatId = null;
                    boolean foundMatch = false;

                    for (EmployeementCategory empCat : empCatList) {
                        if (empCatName.equals(empCat.getCategoryName())) {
                            empCatId = empCat.getId();
                            foundMatch = true;
                            break;  // exit the loop once a match is found
                        }
                    }
                    if (!foundMatch) {
                        jobExcelDto.setEmployee_category_id("invalid");
                    }
                    else {
                        jobExcelDto.setEmployee_category_id(String.valueOf(empCatId));
                    }
                }
                else {
                    jobExcelDto.setEmployee_category_id(null);
                }


                if(rowData9.get(2)!=null && !rowData9.get(2).isEmpty()) {
                    List<StatusMaster> sm = statusMasterService.findAll();
                    System.out.println(sm);
                    String status = rowData9.get(2);
                    Long statusId = null;
                    boolean foundMatch = false;

                    for (StatusMaster statusMaster : sm) {
                        if (status.equals(statusMaster.getStatusName())) {
                            statusId = statusMaster.getId();
                            foundMatch = true;
                            break;  // exit the loop once a match is found
                        }
                    }
                    if (!foundMatch) {
                        jobExcelDto.setStatus_id("invalid");
                    }
                    else {
                        jobExcelDto.setStatus_id(String.valueOf(statusId));
                    }
                }
                else {
                    jobExcelDto.setStatus_id(null);
                }

                if(rowData9.get(4)!=null && !rowData9.get(4).isEmpty()) {
                    List<GradeMaster> gm = gradeService
                            .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                    CommonConstant.ACTIVE, companyId, companyBranchId);
                    System.out.println(gm);
                    String gradeName = rowData9.get(4);
                    Long gradeId = null;
                    boolean foundMatch = false;
                    for (GradeMaster gradeMaster : gm) {
                        if (gradeName.equalsIgnoreCase(gradeMaster.getGradeName())) {
                            gradeId = gradeMaster.getId();
                            foundMatch = true;
                            break;  // exit the loop once a match is found
                        }
                    }if (!foundMatch) {
                        jobExcelDto.setGrade_id("invalid");
                    }
                    else {
                        jobExcelDto.setGrade_id(String.valueOf(gradeId));
                    }
                }
                else {
                    jobExcelDto.setGrade_id(null);
                }


                if(rowData9.get(7)!=null && !rowData9.get(7).isEmpty()) {
                    List<GroupMaster> groupMstList = groupMasterRepository
                            .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                    CommonConstant.ACTIVE, companyId, companyBranchId);
                    System.out.println(groupMstList);
                    String groupName = rowData9.get(7);
                    Long groupId = null;
                    boolean foundMatch = false;

                    for (GroupMaster groupMaster : groupMstList) {
                        if (groupName.equalsIgnoreCase(groupMaster.getGroupName())) {
                            groupId = groupMaster.getId();
                            foundMatch = true;
                            break;  // exit the loop once a match is found
                        }
                    }if (!foundMatch) {
                        jobExcelDto.setGroup_id("invalid");
                    }else {
                        jobExcelDto.setGroup_id(String.valueOf(groupId));
                    }
                }
                else {
                    jobExcelDto.setGroup_id(null);
                }


                if(rowData9.get(9)!=null && !rowData9.get(9).isEmpty()) {
                    List<CompanyMaster>companyMasters  = compRepo.findAllByIsDeleteFalseOrderByCompanyName();
                    System.out.println(companyMasters);
                    String companyName = rowData9.get(9);
                    boolean foundMatch = false;
                    Long companyd = null;
                    for (CompanyMaster companyMaster : companyMasters) {
                        if (companyMaster.getCompanyName().equalsIgnoreCase(companyName)) {
                            companyd = companyMaster.getId();
                            foundMatch = true;
                            break; // Stop searching once a match is found
                        }
                    }if (!foundMatch) {
                        jobExcelDto.setCom_id("invalid");
                    }else {
                        jobExcelDto.setCom_id(String.valueOf(companyd));
                    }
                }
                else {
                    jobExcelDto.setCom_id(null);
                }

//	            if(rowData9.get(10)!=null || !rowData9.get(10).isEmpty()) {
//					List<CompanyBranchMaster> cbm = companyBranchMasterRepository.findbybranchName(rowData9.get(10));
//					jobExcelDto.setBranch_id(cbm.get(0).getId());
//	            }
//	            else {
//	            	jobExcelDto.setBranch_id(null);
//	            }

                if(rowData9.get(11)!=null && !rowData9.get(11).isEmpty()) {
                    List<DesignationMaster> designationMstList = designationMasterRepository
                            .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                    CommonConstant.ACTIVE, companyId, companyBranchId);
                    System.out.println(designationMstList);
                    String designation = rowData9.get(11);
                    Long designationid = null;
                    boolean foundMatch = false;

                    for (DesignationMaster designationMaster : designationMstList) {
                        if (designation.equalsIgnoreCase(designationMaster.getDesignationName())) {
                            designationid = designationMaster.getId();
                            foundMatch = true;
                            break;  // exit the loop once a match is found
                        }
                    }
                    if (!foundMatch) {
                        jobExcelDto.setDestination_id("invalid");
                    }
                    jobExcelDto.setDestination_id(String.valueOf(designationid));
                }
                else {
                    jobExcelDto.setDestination_id(null);
                }

                if(rowData9.get(13)!=null &&!rowData9.get(13).isEmpty()) {
                    List<GroupMaster> groupMstList = groupMasterRepository
                            .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                    CommonConstant.ACTIVE, companyId, companyBranchId);
                    String groupNamewhenjoining = rowData9.get(13);
                    Long groupI = null;
                    boolean foundMatch = false;

                    for (GroupMaster groupMaster : groupMstList) {
                        if (groupNamewhenjoining.equalsIgnoreCase(groupMaster.getGroupName())) {
                            groupI = groupMaster.getId();
                            foundMatch = true;
                            break;  // exit the loop once a match is found
                        }
                    }
//					jobExcelDto.set(groupI);
                }
                else {
//					jobExcelDto.set(groupI);
                }


                if(rowData9.get(16)!=null && !rowData9.get(16).isEmpty()) {
                    List<PayCommissionMaster> pcm = paycommissionService
                            .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                    CommonConstant.ACTIVE, companyId, companyBranchId);
                    String paycommissionname = rowData9.get(16);
                    Long payCommissionId = null;
                    boolean foundMatch = false;

                    for (PayCommissionMaster payCommissionMaster : pcm) {
                        if (payCommissionMaster.getPayCommissionName().equalsIgnoreCase(paycommissionname)) {
                            payCommissionId = payCommissionMaster.getId();
                            foundMatch = true;
                            break; // Stop searching once a match is found
                        }
                    }
                    if (!foundMatch) {
                        jobExcelDto.setPay_commission_id("invalid");
                    }else {
                        jobExcelDto.setPay_commission_id(String.valueOf(payCommissionId));
                    }
                }
                else {
                    jobExcelDto.setPay_commission_id(null);
                }

                if(rowData9.get(17)!=null && !rowData9.get(17).isEmpty()) {
                    List<PayBandMaster> payBandMasters = payBandMasterRepository
                            .findAllByIsDeleteFalseAndStatusIdStatusNameIgnoreCaseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
                                    CommonConstant.ACTIVE, companyId, companyBranchId);
                    String scaleName = rowData9.get(17);
                    Long payBandId = null;
                    boolean foundMatch = false;

                    for (PayBandMaster payBandMaster : payBandMasters) {
                        if (payBandMaster.getPayBandName().equalsIgnoreCase(scaleName)) {
                            payBandId = payBandMaster.getId();
                            foundMatch = true;
                            break; // Stop searching once a match is found
                        }
                    }if (!foundMatch) {
                        jobExcelDto.setPay_band_id("invalid");
                    }else {
                        jobExcelDto.setPay_band_id(String.valueOf(payBandId));//this is scale
                    }
                }
                else {
                    jobExcelDto.setPay_band_id(null);
                }


                if(rowData9.get(19)!=null && !rowData9.get(19).isEmpty()){
                    List<GradePayMaster>gpm =gradePayMasterRepository.findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(companyId,
                            companyBranchId);
                    String gradepay = rowData9.get(19);
                    Long gradepayid = null;
                    boolean foundMatch = false;

                    for (GradePayMaster gradepayMaster : gpm) {
                        String gradepayValueAsString = String.valueOf(gradepayMaster.getGradePayValue());

                        if (gradepayValueAsString.equalsIgnoreCase(gradepay)) {
                            gradepayid = gradepayMaster.getId();
                            foundMatch = true;
                            break; // Stop searching once a match is found
                        }
                    }
                    if (!foundMatch) {
                        jobExcelDto.setGrade_pay_id("invalid");
                    }else {
                        jobExcelDto.setGrade_pay_id(String.valueOf(gradepayid));
                    }
                }
                else {
                    jobExcelDto.setGrade_pay_id(null);
                }


                if(rowData9.get(20)!=null && !rowData9.get(20).isEmpty()) {
                    List<RecruitmentType> recruitmentTypes = recruitmentTypeService.findAllByIsDeleteFalse();
                    String recruitmentTypeName = rowData9.get(20);
                    Long recruitmentTypeId = null;
                    boolean foundMatch = false;

                    for (RecruitmentType recruitmentType : recruitmentTypes) {
                        if (recruitmentType.getRecruitmentTypeName().equalsIgnoreCase(recruitmentTypeName)) {
                            recruitmentTypeId = recruitmentType.getId();
                            foundMatch = true;
                            break; // Stop searching once a match is found
                        }
                    }if (!foundMatch) {
                        jobExcelDto.setRecruitment_type_id("invalid");
                    }else {
                        jobExcelDto.setRecruitment_type_id(String.valueOf(recruitmentTypeId));
                    }
                }
                else {
                    jobExcelDto.setRecruitment_type_id(null);
                }


                if(rowData9.get(21)!=null &&!rowData9.get(21).isEmpty()) {
                    List<ShiftPolicyMaster> shiftPolicyMasters = shiftPolicyMasterRepo
                            .findAllByCompanyIdAndCompanyBranchIdAndIsDeleteFalse(companyId, companyBranchId);
                    String shiftPolicyName = rowData9.get(21);
                    Long shiftPolicyId = null;
                    boolean foundMatch = false;

                    for (ShiftPolicyMaster shiftPolicyMaster : shiftPolicyMasters) {
                        if (shiftPolicyMaster.getShiftName().equalsIgnoreCase(shiftPolicyName)) {
                            shiftPolicyId = shiftPolicyMaster.getId();
                            foundMatch = true;
                            break; // Stop searching once a match is found
                        }
                    }
                    if (!foundMatch) {
                        jobExcelDto.setShift_id("invalid");
                    }else {
                        jobExcelDto.setShift_id(String.valueOf(shiftPolicyId));
                    }
                }
                else {
                    jobExcelDto.setShift_id(null);
                }

//				if(rowData9.get(23)!=null || !rowData9.get(23).isEmpty()) {
//					List<WeeklyOffPolicyMaster> weeklyList = weeklyOffPolicyMaster.findLikePatternNameAndCompanyAndBranch(rowData9.get(23),companyId,companyBranchId);
//					jobExcelDto.setWeekly_off(weeklyList.get(0).getId());
//				}
//				else {
//					jobExcelDto.setWeekly_off(null);
//				}
                if (rowData9.get(23) != null && !rowData9.get(23).isEmpty()) {
                    List<WeeklyOffPolicyMaster> weeklyList = weeklyOffPolicyMaster.findLikePatternNameAndCompanyAndBranch(rowData9.get(23), companyId, companyBranchId);

                    if (!weeklyList.isEmpty()) {
                        jobExcelDto.setWeekly_off(String.valueOf(weeklyList.get(0).getId()));
                    } else {
                        jobExcelDto.setWeekly_off("invlaid");
                    }
                } else {
                    jobExcelDto.setWeekly_off(null);
                }

//				jobExcelDto.setEmployement_type_id(employeementTypeId);
//				jobExcelDto.setEmployee_category_id(empCatId);
//				jobExcelDto.setStatus_id(statusId);
                jobExcelDto.setNotice_period(rowData9.get(3));
//				jobExcelDto.setGrade_id(gradeId);
                jobExcelDto.setRetirement_date(rowData9.get(5));
                jobExcelDto.setDuties_responsibility(rowData9.get(6));
//	            jobExcelDto.setGroup_id(groupId);
                jobExcelDto.setInsurance_effective_from(rowData9.get(8));
                jobExcelDto.setCom_id(String.valueOf(companyId));
//	            jobExcelDto.setBranch_id(cbm.get(0).getId());
//	            jobExcelDto.setDestination_id(designationid);
                jobExcelDto.setPosting_date(rowData9.get(12));
                //jobExcelDto.set(groupI);
                jobExcelDto.setPosting_order_no(rowData9.get(14));
                jobExcelDto.setPosting_order_date(rowData9.get(15));
//	            jobExcelDto.setPay_commission_id(payCommissionId);
//	            jobExcelDto.setPay_band_id(payBandId);//this is scale
//	            jobExcelDto.setGrade_pay_id(gradepayid);
//	            jobExcelDto.setRecruitment_type_id(recruitmentTypeId);
//	            jobExcelDto.setShift_id(shiftPolicyId);
                jobExcelDto.setWeekly_off_effective_from(rowData9.get(22));
//	            jobExcelDto.setWeekly_off(weeklyList.get(0).getId());
                jobExcelDto.setEmployee_code(rowData9.get(24));

                JobExcelDto.add(jobExcelDto);

            }
            System.out.println("inside Job excel dto"+JobExcelDto);

            //for 7th sheet

            XSSFSheet worksheet7 = workbook.getSheetAt(7);

            List<List<String>> excelData7 = new ArrayList<>();
            List<HealthExcelDto> HealthExcelDto = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= worksheet7.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet7.getRow(rowIndex);

                List<String> rowData7 = new ArrayList<>();
                HealthExcelDto healthExcelDto = new HealthExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData7.add(formattedDate);
                    } else {
                        rowData7.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData7.get(cellIndex));
                }
                excelData7.add(rowData7);
                healthExcelDto.setHeight(rowData7.get(0));
                healthExcelDto.setWeight(rowData7.get(1));
//	            healthExcelDto.setBlood_group(rowData7.get(2));
//	            List<HrmsCode> bloodGroupList = hrmsCodeService.findByFieldNamehealth("BLD_GRP");
//	            System.out.println("i am in blood group "+bloodGroupList);
                healthExcelDto.setBlood_group(rowData7.get(2).toString()); // Assuming rowData7 is a list of Objects

                List<HrmsCode> bloodGroupList = hrmsCodeService.findByFieldNamehealth("BLD_GRP");
                List<String> validBloodGroups = new ArrayList<>();

                // Extract blood group values from the bloodGroupList and store them in validBloodGroups list
                for (HrmsCode hrmsCode : bloodGroupList) {
                    validBloodGroups.add(hrmsCode.getDescription());
                }

                if(rowData7.get(2).isEmpty()) {
                    healthExcelDto.setBlood_group(null);
                }

                // Check if the value fetched from the Excel column is in the validBloodGroups list
                else if (validBloodGroups.contains(healthExcelDto.getBlood_group())) {
                    // Set the value as it is valid
                }

                else {
                    // Set it to "invalid" as it is not in the list
                    healthExcelDto.setBlood_group("invalid");
                }

                healthExcelDto.setIdentification_mark_first(rowData7.get(3));
                healthExcelDto.setIdentification_mark_second(rowData7.get(4));
                healthExcelDto.setPhysically_disabled(rowData7.get(5));
                healthExcelDto.setCom_id(companyId);
                healthExcelDto.setBranch_id(companyBranchId);
                healthExcelDto.setEmployee_code(rowData7.get(6));
                healthExcelDto.setCreatedBy(um.getId());

                HealthExcelDto.add(healthExcelDto);
            }
            System.out.println(HealthExcelDto);


            //for 10th sheet
            XSSFSheet worksheet10 = workbook.getSheetAt(10);

            List<List<String>> excelData10 = new ArrayList<>();
            List<ReportingOfficeExcelDto> ReportingOfficeExcelDto = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= worksheet10.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet10.getRow(rowIndex);

                List<String> rowData10 = new ArrayList<>();
                ReportingOfficeExcelDto reportingOfficeExcelDto = new ReportingOfficeExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData10.add(formattedDate);
                    } else {
                        rowData10.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData10.get(cellIndex));
                }
                excelData10.add(rowData10);
                reportingOfficeExcelDto.setEnd_date(rowData10.get(1));
                reportingOfficeExcelDto.setStart_date(rowData10.get(0));
                reportingOfficeExcelDto.setNote(rowData10.get(7));
                reportingOfficeExcelDto.setAuthorization_date(rowData10.get(6));
                reportingOfficeExcelDto.setEmployee_code(rowData10.get(8));

                if (rowData10.get(2) != null && !rowData10.get(2).isEmpty()) {
                    List<Employee> ddo = employeeRepository.findAllByIsDeleteFalse(rowData10.get(2),companyId,companyBranchId);
//		            reportingOfficeExcelDto.setDdo_id(ddo.get(0).getId());
                    if (ddo != null &&!ddo.isEmpty()) {
                        reportingOfficeExcelDto.setDdo_id(String.valueOf(ddo.get(0).getId()));
                    }
                    else {
                        reportingOfficeExcelDto.setDdo_id("invalid");
                    }
                }

                else {
                    reportingOfficeExcelDto.setDdo_id(null);
                }


                if (rowData10.get(3) != null && !rowData10.get(3).isEmpty()) {
                    List<Employee> dh = employeeRepository.findAllByIsDeleteFalse(rowData10.get(3),companyId,companyBranchId);
//		            reportingOfficeExcelDto.setDh_id(dh.get(0).getId());
                    if (dh != null &&!dh.isEmpty()) {
                        reportingOfficeExcelDto.setDh_id(String.valueOf(dh.get(0).getId()));
                    }
                    else {
                        reportingOfficeExcelDto.setDh_id("invalid");
                    }
                }
                else {
                    reportingOfficeExcelDto.setDh_id(null);
                }

                if (rowData10.get(4) != null && !rowData10.get(4).isEmpty()) {
                    List<Employee> ho = employeeRepository.findAllByIsDeleteFalse(rowData10.get(4),companyId,companyBranchId);
//		            reportingOfficeExcelDto.setHo_id(ho.get(0).getId());
                    if (ho != null &&!ho.isEmpty()) {
                        reportingOfficeExcelDto.setHo_id(String.valueOf(ho.get(0).getId()));
                    }
                    else {
                        reportingOfficeExcelDto.setHo_id("invalid");
                    }
                }
                else {
                    reportingOfficeExcelDto.setHo_id(null);
                }

                if (rowData10.get(5) != null && !rowData10.get(5).isEmpty()) {
                    List<Employee> hod = employeeRepository.findAllByIsDeleteFalse(rowData10.get(5),companyId,companyBranchId);
//		            reportingOfficeExcelDto.setHod_id(hod.get(0).getId());
                    if (hod != null &&!hod.isEmpty()) {
                        reportingOfficeExcelDto.setHod_id(String.valueOf(hod.get(0).getId()));
                    }
                    else {
                        reportingOfficeExcelDto.setHod_id("invalid");
                    }
                }
                else {
                    reportingOfficeExcelDto.setHod_id(null);
                }

                ReportingOfficeExcelDto.add(reportingOfficeExcelDto);
            }
            System.out.println(ReportingOfficeExcelDto);

            //for 11th Sheet
            XSSFSheet worksheet11 = workbook.getSheetAt(11);

            List<List<String>> excelData11 = new ArrayList<>();
            List<PreviousEmployementExcelDto> PreviousEmployementExcelDto = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= worksheet11.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet11.getRow(rowIndex);

                List<String> rowData11 = new ArrayList<>();
                PreviousEmployementExcelDto previousEmployementExcelDto = new PreviousEmployementExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData11.add(formattedDate);
                    } else {
                        rowData11.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData11.get(cellIndex));
                }
                excelData11.add(rowData11);
                previousEmployementExcelDto.setFrom_date(rowData11.get(0));
                previousEmployementExcelDto.setTo_date(rowData11.get(1));
                previousEmployementExcelDto.setCompanyName(rowData11.get(2));
//	            previousEmployementExcelDto.setService_type(rowData11.get(3));
                String serviceTypeInput = rowData11.get(3);

                if (serviceTypeInput != null && !serviceTypeInput.isEmpty() ) {
                    // Convert to lowercase for case-insensitivity
                    String lowerCaseInput = serviceTypeInput.toLowerCase().trim();

                    if ("part time".equals(lowerCaseInput) || "full time".equals(lowerCaseInput)) {
                        previousEmployementExcelDto.setService_type(serviceTypeInput);
                    } else {
                        previousEmployementExcelDto.setService_type("invalid");
                    }
                } else {
                    previousEmployementExcelDto.setService_type(null);
                }
                previousEmployementExcelDto.setPosition(rowData11.get(4));
                previousEmployementExcelDto.setLast_ctc(rowData11.get(5));
                previousEmployementExcelDto.setResponsibilities(rowData11.get(6));
                previousEmployementExcelDto.setReason_for_separation(rowData11.get(7));
                previousEmployementExcelDto.setHr_contact_person(rowData11.get(8));
                previousEmployementExcelDto.setPhone_no(rowData11.get(9));
                previousEmployementExcelDto.setMobile_no(rowData11.get(10));
                previousEmployementExcelDto.setEmail(rowData11.get(11));
                previousEmployementExcelDto.setWebsite_url(rowData11.get(12));
                previousEmployementExcelDto.setAddress(rowData11.get(13));
                previousEmployementExcelDto.setRemark(rowData11.get(14));
                previousEmployementExcelDto.setEmployee_code(rowData11.get(15));

                PreviousEmployementExcelDto.add(previousEmployementExcelDto);
            }
            System.out.println(PreviousEmployementExcelDto);

            //for 12th Sheet
            XSSFSheet worksheet12 = workbook.getSheetAt(12);

            List<List<String>> excelData12 = new ArrayList<>();
            List<EducationQualificationExcelDto> EducationQualificationExcelDto = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= worksheet12.getLastRowNum(); rowIndex++) {
                XSSFRow row = worksheet12.getRow(rowIndex);

                List<String> rowData12 = new ArrayList<>();
                EducationQualificationExcelDto educationQualificationExcelDto = new EducationQualificationExcelDto();

                // Loop through cells in the row
                for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                    XSSFCell cell = row.getCell(cellIndex);

                    // Format the cell value
                    String formattedValue = dataFormatter.formatCellValue(cell);

                    // Check if the formatted value looks like a date
                    if (isFormattedAsDate(formattedValue)) {
                        Date dateValue = cell.getDateCellValue();
                        String formattedDate = dateFormatter.format(dateValue);
                        rowData12.add(formattedDate);
                    } else {
                        rowData12.add(formattedValue);
                    }
                    System.out.println("Row " + rowIndex + ", Cell " + cellIndex + ": " + rowData12.get(cellIndex));
                }
                excelData12.add(rowData12);

//	            List<EducationMaster> em = educationMasterRepository.findAllByIsDeleteFalseAndEducationName(rowData12.get(0),companyId,companyBranchId);
//	            System.out.println(em);
//	            educationQualificationExcelDto.setQualification(em.get(0).getId());

                if (rowData12.get(0) != null && !rowData12.get(0).isEmpty()) {
                    List<EducationMaster> em = educationMasterRepository.findAllByIsDeleteFalseAndEducationName(rowData12.get(0),companyId,companyBranchId);
                    if (em != null &&!em.isEmpty()) {
                        educationQualificationExcelDto.setQualification(String.valueOf(em.get(0).getId()));
                    }
                    else {
                        educationQualificationExcelDto.setQualification("invalid");
                    }
                }
                else {
                    educationQualificationExcelDto.setQualification(null);
                }
//	            educationQualificationExcelDto.setMode_of_study(rowData12.get(1));

                educationQualificationExcelDto.setMode_of_study(rowData12.get(1).toString()); // Assuming rowData7 is a list of Objects

                List<HrmsCode> StudyMode = hrmsCodeService.findByFieldName("STUDY_MODE");
                List<String> validStudyMode = new ArrayList<>();


                for (HrmsCode hrmsCode : StudyMode) {
                    validStudyMode.add(hrmsCode.getDescription());
                }

                if(rowData12.get(1).isEmpty()) {
                    educationQualificationExcelDto.setMode_of_study(null);
                }

                // Check if the value fetched from the Excel column is in the list
                else if (validStudyMode.contains(educationQualificationExcelDto.getMode_of_study())) {
                    // Set the value as it is valid
                }

                else {
                    // Set it to "invalid" as it is not in the list
                    educationQualificationExcelDto.setMode_of_study("invalid");
                }


                educationQualificationExcelDto.setUniversity(rowData12.get(2));
                //for setting month
                String monthValue = rowData12.get(3);
                String abbreviatedMonth = null;

                if (monthValue != null && !monthValue.isEmpty()) {
                    abbreviatedMonth = monthValue.substring(0, Math.min(monthValue.length(), 3)).toLowerCase();
                }

                educationQualificationExcelDto.setPassing_of_month(abbreviatedMonth);
                educationQualificationExcelDto.setInstitute(rowData12.get(4));
                educationQualificationExcelDto.setPassing_year(rowData12.get(5));
                educationQualificationExcelDto.setDuration(rowData12.get(6));
                educationQualificationExcelDto.setMajor(rowData12.get(7));
                educationQualificationExcelDto.setPercentage(rowData12.get(8));
//	            educationQualificationExcelDto.setGrade(rowData12.get(9));
//	            String gradeValue = rowData12.get(9);
//	            String normalizedGrade="";
//
//	            if (gradeValue != null) {
//	                switch (gradeValue.trim().toUpperCase()) {
//	                    case "A-GRADE":
//	                        normalizedGrade = "A";
//	                        break;
//	                    case "B-GRADE":
//	                        normalizedGrade = "B";
//	                        break;
//	                    case "C-GRADE":
//	                        normalizedGrade = "C";
//	                        break;
//	                    default:
//	                        normalizedGrade = gradeValue; // Use the original value if no match
//	                        break;
//	                }
//	            }
//	            educationQualificationExcelDto.setGrade(normalizedGrade);
                educationQualificationExcelDto.setGrade(rowData12.get(9).toString()); // Assuming rowData7 is a list of Objects

                List<HrmsCode> GradeList = hrmsCodeService.findByFieldName("GRADE");
                List<String> validGradeList = new ArrayList<>();


                for (HrmsCode hrmsCode : GradeList) {
                    validGradeList.add(hrmsCode.getDescription());
                }

                if(rowData12.get(9).isEmpty()) {
                    educationQualificationExcelDto.setGrade(null);
                }

                // Check if the value fetched from the Excel column is in the list
                else if (validGradeList.contains(educationQualificationExcelDto.getGrade())) {
                    // Set the value as it is valid
                }

                else {
                    // Set it to "invalid" as it is not in the list
                    educationQualificationExcelDto.setGrade("invalid");
                }
                educationQualificationExcelDto.setPercentile(rowData12.get(10));
                educationQualificationExcelDto.setGpa_score(rowData12.get(11));
                educationQualificationExcelDto.setRemark(rowData12.get(12));
                educationQualificationExcelDto.setInstitute_address(rowData12.get(13));
                educationQualificationExcelDto.setEmployee_code(rowData12.get(14));
                educationQualificationExcelDto.setCom_id(companyId);
                educationQualificationExcelDto.setBranch_id(companyBranchId);
                EducationQualificationExcelDto.add(educationQualificationExcelDto);
            }
            System.out.println(EducationQualificationExcelDto);

            // Close the workbook after processing
            workbook.close();


            AjaxResponseBody response = new AjaxResponseBody();
            session.setAttribute("ajaxResponse", response);
            response.setEmployeeExcelDtos(EmployeeExcelDto);
            response.setPersonalExcelDtos(PersonalExcelDto);
            response.setContactExcelDtos(ContactExcelDto);
            response.setAddressExcelDtos(AddressExcelDto);
            response.setFamilyExcelDtos(FamilyExcelDto);
            response.setEmergencyExcelDtos(EmergencyExcelDto);
            response.setNomineeExcelDtos(NomineeExcelDto);
            response.setHealthExcelDtos(HealthExcelDto);
            response.setJobExcelDtos(JobExcelDto);
            response.setPreviousEmployementExcelDtos(PreviousEmployementExcelDto);
            response.setEducationQualificationExcelDtos(EducationQualificationExcelDto);
            response.setReportingOfficeExcelDtos(ReportingOfficeExcelDto);

            response.setStatus("SUCCESS");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AjaxResponseBody();
    }


    private boolean isFormattedAsDate(String formattedValue) {
        try {
            // Try parsing the formatted value as a date
            SimpleDateFormat dateFormatter = new SimpleDateFormat();
            dateFormatter.setLenient(false);
            dateFormatter.parse(formattedValue);
            return true;
        } catch (ParseException e) {
            // Parsing failed, not formatted as a date
            return false;
        }
    }

    @PostMapping(value = "/validateData")
    @ResponseBody
    public AjaxResponseBody validateData(
            HttpServletRequest request,
            HttpSession session) {
        AjaxResponseBody response = new AjaxResponseBody();

        try {
            Long userId = (long) session.getAttribute("userId");
            UserMaster um = (UserMaster) session.getAttribute("usermaster");
            Long companyId = (Long) session.getAttribute("companyId");
            Long companyBranchId = (Long) session.getAttribute("companyBranchId");

            // Assuming you have stored AjaxResponseBody in the session
            AjaxResponseBody ajaxResponseBody = (AjaxResponseBody) session.getAttribute("ajaxResponse");

            if (ajaxResponseBody != null) {
                List<EmployeeExcelDto> employeeExcelDtos = ajaxResponseBody.getEmployeeExcelDtos();

                // Print the items in the employeeExcelDtos list
                if (employeeExcelDtos != null) {
                    for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {

                        if(employeeRepository.existsByEmpCodeIgnoreCaseOrPanNumberAndComId( employeeExcelDto.getEmp_code(),employeeExcelDto.getPan_number(),  companyId)){
                            response.setStatus("FAIL");
                            response.setErrorMessage("An employee with the  same Code Or Same Pan Number already exists: " );
                            return response;

                        }




                        if (employeeExcelDto.getEmp_code_postfix() == null || employeeExcelDto.getEmp_code_postfix().isEmpty()||
                                employeeExcelDto.getPan_number() == null || employeeExcelDto.getPan_number().isEmpty()||
                                employeeExcelDto.getDate_of_appointment() == null || employeeExcelDto.getDate_of_appointment().isEmpty()||
                                employeeExcelDto.getSalutation() == null || employeeExcelDto.getSalutation().isEmpty()||
                                employeeExcelDto.getDepartment() == null ||
                                employeeExcelDto.getFirst_name() == null || employeeExcelDto.getFirst_name().isEmpty()||
                                employeeExcelDto.getDesignation() == null ||
                                employeeExcelDto.getGroup() == null ||
                                employeeExcelDto.getEmployee_eligible_for() == null || employeeExcelDto.getEmployee_eligible_for().isEmpty()) {

                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields for employee: " + employeeExcelDto.getFirst_name());
                            return response; // Stop the iteration and return "FAIL" status
                        }
                        if(!isValidPANNumber(employeeExcelDto.getPan_number())){
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Pan Number for employee: " + employeeExcelDto.getFirst_name());
                            return response;
                        }
                        if(employeeExcelDto.getDepartment()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Department for employee: " + employeeExcelDto.getFirst_name());
                            return response;
                        }
                        if(employeeExcelDto.getDesignation()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Designation for employee: " + employeeExcelDto.getFirst_name());
                            return response;
                        }
                        if(employeeExcelDto.getGroup()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Group for employee: " + employeeExcelDto.getFirst_name());
                            return response;
                        }
                        if(employeeExcelDto.getUnit()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Unit for employee: " + employeeExcelDto.getFirst_name());
                            return response;
                        }
                    }
                } else {
                    System.out.println("Employee Excel Dtos list is null");
                }

                //for personal tab
                List<PersonalExcelDto> personalExcelDtos = ajaxResponseBody.getPersonalExcelDtos();
                if(personalExcelDtos != null) {
                    for(PersonalExcelDto personalExcelDto : personalExcelDtos) {
                        if(personalExcelDto.getGender()==null|| personalExcelDto.getGender().isEmpty()||
                                personalExcelDto.getDate_of_birth()==null|| personalExcelDto.getDate_of_birth().isEmpty()||
                                personalExcelDto.getCategory()==null|| personalExcelDto.getCategory().isEmpty()||
                                personalExcelDto.getBank_id()==null|| personalExcelDto.getBank_id().isEmpty()||
                                personalExcelDto.getBank_branch_id()==null|| personalExcelDto.getBank_branch_id().isEmpty()||
                                personalExcelDto.getBank_acc_no()==null|| personalExcelDto.getBank_acc_no().isEmpty()||
                                personalExcelDto.getIfsc_code()==null||personalExcelDto.getIfsc_code().isEmpty())
                        {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in personal information: " );;
                            return response;
                        }
//////////////////////////////
                        String dateOfBirth = personalExcelDto.getDate_of_birth();
                        String employeeCode = personalExcelDto.getEmployee_code();

                        // Find the corresponding EmployeeExcelDto
                        EmployeeExcelDto correspondingEmployee = findEmployeeByCode(employeeCode, employeeExcelDtos);

                        if (correspondingEmployee != null) {
                            // Use the validateAppointmentDate function to check the difference
                            String dateOfAppointment = correspondingEmployee.getDate_of_appointment();

                            if (!validateAppointmentDate(dateOfAppointment, dateOfBirth)) {
                                response.setStatus("FAIL");
                                response.setErrorMessage("Minimum 18 years gap between Date of Appointment and Date of Birth for Employee Code: " + employeeCode);
                                return response;
                            }
                        } else {
                            // Handle case where no corresponding employee is found
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + employeeCode);
                            return response;
                        }
/////////////////////////////
                        if(!isValidACNumber (personalExcelDto.getBank_acc_no())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Bank Account Number for Employee Code: " + personalExcelDto.getEmployee_code());;
                            return response;
                        }

                        if(!isValidIFSC (personalExcelDto.getIfsc_code())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Bank IFSC Code for Employee Code: " + personalExcelDto.getEmployee_code());;
                            return response;
                        }

                        if(!personalExcelDto.getBsr_code().isEmpty()&&!isValidBSR (personalExcelDto.getBsr_code())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct BSR Code for Employee Code: " + personalExcelDto.getEmployee_code());;
                            return response;
                        }

                        if(!personalExcelDto.getPassport_no().isEmpty()&&!isValidPassportNo (personalExcelDto.getPassport_no())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Passport Number for Employee Code: " + personalExcelDto.getEmployee_code());;
                            return response;
                        }
                        if(personalExcelDto.getReligion_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Religion for Employee Code: " + personalExcelDto.getEmployee_code());;
                            return response;
                        }
                        if(personalExcelDto.getCast_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Cast for Employee Code: " + personalExcelDto.getEmployee_code());;
                            return response;
                        }
                        if(personalExcelDto.getCountry_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Country for Employee Code: " + personalExcelDto.getEmployee_code());;
                            return response;
                        }
                        if(personalExcelDto.getBank_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Bank for Employee Code: " + personalExcelDto.getEmployee_code());;
                            return response;
                        }
                        if(personalExcelDto.getBank_branch_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Bank Branch for Employee Code: " + personalExcelDto.getEmployee_code());;
                            return response;
                        }
                        if(personalExcelDto.getState_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct State for Employee Code: " + personalExcelDto.getEmployee_code());;
                            return response;
                        }
                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(personalExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + personalExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("Personal Excel Dtos list is null");
                }

                //for contact tab
                List<ContactExcelDto> contactExcelDtos = ajaxResponseBody.getContactExcelDtos();
                if(contactExcelDtos != null) {
                    for(ContactExcelDto contactExcelDto : contactExcelDtos) {
                        if(contactExcelDto.getCorporate_email()==null|| contactExcelDto.getCorporate_email().isEmpty())
                        {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in Contact information: " );
                            return response;
                        }
                        if(!contactExcelDto.getHome_mobile().isEmpty() && !isValidMobileNumber(contactExcelDto.getHome_mobile())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Mobile Number for Employee Code:" + contactExcelDto.getEmployee_code());
                            return response;
                        }
                        if(!contactExcelDto.getWork_mobile().isEmpty() && !isValidMobileNumber(contactExcelDto.getWork_mobile())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Work Mobile Number for Employee Code:" + contactExcelDto.getEmployee_code());
                            return response;
                        }
                        if(!contactExcelDto.getWork_phone().isEmpty() && !isValidMobileNumber(contactExcelDto.getWork_phone())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Work Phone Number for Employee Code:" + contactExcelDto.getEmployee_code());
                            return response;
                        }
                        if(!contactExcelDto.getHome_phone().isEmpty() && !isValidMobileNumber(contactExcelDto.getHome_phone())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Home Phone Number for Employee Code:" + contactExcelDto.getEmployee_code());
                            return response;
                        }
                        if(!isValidEmail(contactExcelDto.getCorporate_email())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Coporate Email for Employee Code:" + contactExcelDto.getEmployee_code());
                            return response;
                        }
                        if(!contactExcelDto.getPersonal_email().isEmpty() && !isValidEmail(contactExcelDto.getPersonal_email())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct personal Email for Employee Code:" + contactExcelDto.getEmployee_code());
                            return response;
                        }
//	            		if(!contactExcelDto.getSecondary_email().isEmpty() && !isValidEmail(contactExcelDto.getSecondary_email())) {
//	            			response.setStatus("FAIL");
//                            response.setErrorMessage("Please fill Correct Secondary Email for Employee Code:" + contactExcelDto.getEmployee_code());
//                            return response;
//	            		}
                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(contactExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + contactExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("Contact Excel Dtos list is null");
                }
                //for address tab
                List<AddressExcelDto> addressExcelDtos = ajaxResponseBody.getAddressExcelDtos();
                if(addressExcelDtos != null) {
                    for(AddressExcelDto addressExcelDto : addressExcelDtos) {
                        if(addressExcelDto.getAddress_type()==null|| addressExcelDto.getAddress_type().isEmpty()||
                                addressExcelDto.getAddress()==null|| addressExcelDto.getAddress().isEmpty()||
                                addressExcelDto.getCountry()==null ||
                                addressExcelDto.getState()==null ||
                                addressExcelDto.getDistrict()==null ||
                                addressExcelDto.getCity()==null||
                                addressExcelDto.getPincode()==null||addressExcelDto.getPincode().isEmpty()
                        ){
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in Address information: " );;
                            return response;
                        }
                        if(addressExcelDto.getCountry()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Country for Employee Code: " + addressExcelDto.getEmployee_code());;
                            return response;
                        }
                        if(addressExcelDto.getState()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct State for Employee Code: " + addressExcelDto.getEmployee_code());;
                            return response;
                        }
                        if(addressExcelDto.getDistrict()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct District for Employee Code: " + addressExcelDto.getEmployee_code());;
                            return response;
                        }
                        if(addressExcelDto.getCity()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct City for Employee Code: " + addressExcelDto.getEmployee_code());;
                            return response;
                        }
                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(addressExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + addressExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("Address Excel Dtos list is null");
                }

                // for family tab
                List<FamilyExcelDto> familyExcelDtos = ajaxResponseBody.getFamilyExcelDtos();
                if(familyExcelDtos != null) {
                    for(FamilyExcelDto familyExcelDto : familyExcelDtos) {
                        if(familyExcelDto.getFirst_name()==null||familyExcelDto.getFirst_name().isEmpty()||
                                familyExcelDto.getRelation()==null||
                                familyExcelDto.getDate_of_birth()==null||familyExcelDto.getDate_of_birth().isEmpty())
                        {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in Family information: " );;
                            return response;
                        }
                        if(familyExcelDto.getRelation()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Relation for Employee:"+ familyExcelDto.getEmployee_code());;
                            return response;
                        }
                        if(familyExcelDto.getNationality()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Nationality for Employee:"+ familyExcelDto.getEmployee_code());;
                            return response;
                        }
                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(familyExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + familyExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("Family Excel Dtos list is null");
                }

                //for emergency tab
                List<EmergencyExcelDto> emergencyExcelDtos = ajaxResponseBody.getEmergencyExcelDtos();
                if(emergencyExcelDtos != null) {
                    for(EmergencyExcelDto emergencyExcelDto : emergencyExcelDtos) {
                        if(emergencyExcelDto.getFirst_name()==null||emergencyExcelDto.getFirst_name().isEmpty()||
                                emergencyExcelDto.getRelation()==null||
                                emergencyExcelDto.getMobile_no()==null||emergencyExcelDto.getMobile_no().isEmpty())
                        {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in Emergency information: " );;
                            return response;
                        }
                        if(!isValidMobileNumber(emergencyExcelDto.getMobile_no())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Mobile Number for Employee Code: " +emergencyExcelDto.getEmployee_code());
                            return response;
                        }
                        if(!emergencyExcelDto.getEmail().isEmpty() && !isValidEmail(emergencyExcelDto.getEmail())) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Email for Employee Code: " +emergencyExcelDto.getEmployee_code());
                            return response;
                        }
                        if(emergencyExcelDto.getRelation()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Relation Name for Employee Code: " +emergencyExcelDto.getEmployee_code());
                            return response;
                        }
                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(emergencyExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + emergencyExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("Emergency Excel Dtos list is null");
                }

                //for nominee tab
                List<NomineeExcelDto> nomineeExcelDtos = ajaxResponseBody.getNomineeExcelDtos();
                if(nomineeExcelDtos != null) {
                    for(NomineeExcelDto nomineeExcelDto : nomineeExcelDtos) {
                        if(nomineeExcelDto.getFirst_name()==null||nomineeExcelDto.getFirst_name().isEmpty()||
                                nomineeExcelDto.getRelation()==null||
                                nomineeExcelDto.getDate_of_birth()==null||nomineeExcelDto.getDate_of_birth().isEmpty())
                        {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in Nominee information: " );;
                            return response;
                        }
                        if(!nomineeExcelDto.getPan_no().isEmpty() && !isValidPANNumber(nomineeExcelDto.getPan_no())){
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Pan No.in Nominee informationfor Employee:"+ nomineeExcelDto.getEmployee_code());
                            return response;
                        }
                        if(!nomineeExcelDto.getContact_no().isEmpty() && !isValidMobileNumber(nomineeExcelDto.getContact_no())){
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Contact Number in Nominee informationfor Employee:"+ nomineeExcelDto.getEmployee_code());
                            return response;
                        }
                        if(nomineeExcelDto.getRelation()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Relation for Employee:"+ nomineeExcelDto.getEmployee_code());
                            return response;
                        }
                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(nomineeExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + nomineeExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("Nominee Excel Dtos list is null");
                }

                //for job tab

                List<JobExcelDto> jobExcelDtos = ajaxResponseBody.getJobExcelDtos();
                if(jobExcelDtos != null) {
                    for(JobExcelDto jobExcelDto : jobExcelDtos) {
                        if(jobExcelDto.getEmployement_type_id()==null||
                                jobExcelDto.getEmployee_category_id()==null||
                                jobExcelDto.getStatus_id()==null||
                                jobExcelDto.getRecruitment_type_id()==null)
                        {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in Job information:for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getEmployement_type_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Employement type for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getEmployee_category_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Employement Category for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getStatus_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Status for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getGrade_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Grade for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getGroup_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Group for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getCom_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Company for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getDestination_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Designation for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getPay_commission_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Pay Commission for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getPay_band_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Pay Band for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getGrade_pay_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Grade Pay for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getRecruitment_type_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Recruitment Type for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getShift_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Shift for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        if(jobExcelDto.getWeekly_off()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Weekly off for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(jobExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + jobExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("Job Excel Dtos list is null");
                }

                //for health tab
                List<HealthExcelDto> healthExcelDtos = ajaxResponseBody.getHealthExcelDtos();
                if(healthExcelDtos != null) {
                    for(HealthExcelDto healthExcelDto : healthExcelDtos) {
                        if(healthExcelDto.getHeight()==null||healthExcelDto.getHeight().isEmpty()||
                                healthExcelDto.getBlood_group()==null || healthExcelDto.getBlood_group().isEmpty())
                        {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in Health information: " );;
                            return response;
                        }
                        if(healthExcelDto.getBlood_group()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Blood Group for Employee Code: " +healthExcelDto.getEmployee_code());
                            return response;
                        }
                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(healthExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + healthExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("Health Excel Dtos list is null");
                }

                //for previous employement tab
                List<PreviousEmployementExcelDto> previousEmployementExcelDtos = ajaxResponseBody.getPreviousEmployementExcelDtos();
                if(previousEmployementExcelDtos != null) {
                    for(PreviousEmployementExcelDto previousEmployementExcelDto : previousEmployementExcelDtos) {
                        if(previousEmployementExcelDto.getTo_date()==null||
                                previousEmployementExcelDto.getFrom_date()==null || previousEmployementExcelDto.getFrom_date().isEmpty()||
                                previousEmployementExcelDto.getCompanyName()==null || previousEmployementExcelDto.getCompanyName().isEmpty()||
                                previousEmployementExcelDto.getService_type()==null || previousEmployementExcelDto.getService_type().isEmpty())
                        {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in Previous Employement information: " );
                            return response;
                        }
                        if(!previousEmployementExcelDto.getPhone_no().isEmpty() && !isValidPhoneNumber(previousEmployementExcelDto.getPhone_no()))	{
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Phone Number for Employee Code:" +previousEmployementExcelDto.getEmployee_code());
                            return response;
                        }
                        if(!previousEmployementExcelDto.getMobile_no().isEmpty() && !isValidMobileNumber(previousEmployementExcelDto.getMobile_no()))	{
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Mobile Number for Employee Code:" +previousEmployementExcelDto.getEmployee_code());
                            return response;
                        }
                        if(!previousEmployementExcelDto.getEmail().isEmpty() && !isValidEmail(previousEmployementExcelDto.getEmail()))	{
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Correct Email for Employee Code:" +previousEmployementExcelDto.getEmployee_code());
                            return response;
                        }
                        if(previousEmployementExcelDto.getService_type() =="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Service Type for Employee Code: " +previousEmployementExcelDto.getEmployee_code());
                            return response;
                        }
                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(previousEmployementExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + previousEmployementExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("PrevioysEmployement Excel Dtos list is null");
                }

                //for education qualification

                List<EducationQualificationExcelDto> educationQualificationExcelDtos = ajaxResponseBody.getEducationQualificationExcelDtos();
                if(educationQualificationExcelDtos != null) {
                    for(EducationQualificationExcelDto educationQualificationExcelDto : educationQualificationExcelDtos) {
                        if(educationQualificationExcelDto.getQualification()==null||
                                educationQualificationExcelDto.getPassing_of_month()==null || educationQualificationExcelDto.getPassing_of_month().isEmpty()||
                                educationQualificationExcelDto.getPassing_year()==null || educationQualificationExcelDto.getPassing_year().isEmpty())	            		{
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in Education Qualification tab: " );;
                            return response;
                        }
                        if(educationQualificationExcelDto.getQualification() =="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Qualification for Employee Code: " +educationQualificationExcelDto.getEmployee_code());
                            return response;
                        }

                        if(educationQualificationExcelDto.getMode_of_study() =="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Mode Of Study for Employee Code: " +educationQualificationExcelDto.getEmployee_code());
                            return response;
                        }

                        if(educationQualificationExcelDto.getGrade() =="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Grade for Employee Code: " +educationQualificationExcelDto.getEmployee_code());
                            return response;
                        }
                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(educationQualificationExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + educationQualificationExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("Education qualification Excel Dtos list is null");
                }
                //  for reporting officer
                List<ReportingOfficeExcelDto> reportingOfficeExcelDtos = ajaxResponseBody.getReportingOfficeExcelDtos();
                if(reportingOfficeExcelDtos != null) {
                    for(ReportingOfficeExcelDto reportingOfficeExcelDto : reportingOfficeExcelDtos) {
                        if(reportingOfficeExcelDto.getStart_date()==null||reportingOfficeExcelDto.getStart_date().isEmpty()||
                                reportingOfficeExcelDto.getDh_id()==null ||
                                reportingOfficeExcelDto.getHo_id()==null ||
                                reportingOfficeExcelDto.getHod_id()==null||
                                reportingOfficeExcelDto.getAuthorization_date()==null||reportingOfficeExcelDto.getAuthorization_date().isEmpty()
                        )	            		{
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please fill Mandatory fields in Reporting Officer tab: " );;
                            return response;
                        }
                        if(reportingOfficeExcelDto.getDdo_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Assigned Ddo for Employee Code: " +reportingOfficeExcelDto.getEmployee_code());
                            return response;
                        }

                        if(reportingOfficeExcelDto.getDh_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Assigned Dh for Employee Code: " +reportingOfficeExcelDto.getEmployee_code());
                            return response;
                        }

                        if(reportingOfficeExcelDto.getHo_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Assigned HO for Employee Code: " +reportingOfficeExcelDto.getEmployee_code());
                            return response;
                        }

                        if(reportingOfficeExcelDto.getHod_id()=="invalid") {
                            response.setStatus("FAIL");
                            response.setErrorMessage("Please Enter Correct Assigned HOD for Employee Code: " +reportingOfficeExcelDto.getEmployee_code());
                            return response;
                        }

                        boolean foundMatchingEmployee = false;
                        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                            if (employeeExcelDto.getEmp_code_postfix().equals(reportingOfficeExcelDto.getEmployee_code())) {
                                foundMatchingEmployee = true;
                                break;  // Assuming each EmployeeExcelDto has a unique emp_code_postfix
                            }
                        }
                        if (!foundMatchingEmployee) {
                            response.setStatus("FAIL");
                            response.setErrorMessage("No matching employee found for Employee Code: " + reportingOfficeExcelDto.getEmployee_code());
                            return response;
                        }
                    }
                }
                else {
                    System.out.println("Reporting Officer Excel Dtos list is null");
                }


                //

            } else {
                System.out.println("AjaxResponseBody is null in the session");
            }

            response.setStatus("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @PostMapping(value = "/saveData")
    @ResponseBody
    public String saveDataAndSync(
            HttpServletRequest request,
            HttpSession session) {
        AjaxResponseBody responses = new AjaxResponseBody();
        Long companyyId = (Long) session.getAttribute("companyId");
        Long companyBranchId = (Long) session.getAttribute("companyBranchId");

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            UserMaster um = (UserMaster) session.getAttribute("usermaster");

            AjaxResponseBody ajaxResponseBody = (AjaxResponseBody) session.getAttribute("ajaxResponse");

            List<EmployeeExcelDto> employeeExcelDtos = ajaxResponseBody.getEmployeeExcelDtos();
            List<PersonalExcelDto> personalExcelDtos = ajaxResponseBody.getPersonalExcelDtos();
            List<ContactExcelDto> contactExcelDtos = ajaxResponseBody.getContactExcelDtos();
            List<AddressExcelDto> addressExcelDtos = ajaxResponseBody.getAddressExcelDtos();
            List<FamilyExcelDto> familyExcelDtos = ajaxResponseBody.getFamilyExcelDtos();
            List<EmergencyExcelDto> emergencyExcelDtos = ajaxResponseBody.getEmergencyExcelDtos();
            List<NomineeExcelDto> nomineeExcelDtos= ajaxResponseBody.getNomineeExcelDtos();
            List<JobExcelDto> JobExcelDtos= ajaxResponseBody.getJobExcelDtos();
            List<HealthExcelDto> HealthExcelDtos = ajaxResponseBody.getHealthExcelDtos();
            List<PreviousEmployementExcelDto> previousEmployementExcelDtos = ajaxResponseBody.getPreviousEmployementExcelDtos();
            List<EducationQualificationExcelDto> educationQualificationExcelDtos = ajaxResponseBody.getEducationQualificationExcelDtos();	        Long savedEmpId = null;
            List<ReportingOfficeExcelDto> reportingOfficeExcelDtos = ajaxResponseBody.getReportingOfficeExcelDtos();

            for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
                Employee em = new Employee();

                em.setEmpCodePrefix(employeeExcelDto.getEmp_code_prefix());
                em.setEmpCodePostfix(employeeExcelDto.getEmp_code_postfix());
                em.setEmpCode(employeeExcelDto.getEmp_code());
                em.setOldEmpCode(employeeExcelDto.getOld_emp_code());
                Date dateOfAppointment = dateFormatter.parse(employeeExcelDto.getDate_of_appointment());
                em.setDateOfAppointment(dateOfAppointment);
                em.setBioMetricId(employeeExcelDto.getBio_metric_id());
                em.setSalutation(employeeExcelDto.getSalutation());
                em.setFirstName(employeeExcelDto.getFirst_name());
                em.setMiddleName(employeeExcelDto.getMiddle_name());
                em.setLastName(employeeExcelDto.getLast_name());

                DepartmentMaster departmentMaster = new DepartmentMaster();
                departmentMaster.setId(Long.parseLong(employeeExcelDto.getDepartment()));
                em.setDepartment(departmentMaster);

                DesignationMaster designationMaster = new DesignationMaster();
                designationMaster.setId(Long.parseLong(employeeExcelDto.getDesignation()));
                em.setDesignation(designationMaster);

                GroupMaster groupMaster = new GroupMaster();
                groupMaster.setId(Long.parseLong(employeeExcelDto.getGroup()));
                em.setGroup(groupMaster);
                em.setCreatedBy(um.getId());
                em.setEmpUnderGratuityAct(employeeExcelDto.getEmp_under_gratuity_act());
                Long companyId = employeeExcelDto.getCom();
                CompanyMaster companyMaster = null;
                if (companyId != null) {
                    Optional<CompanyMaster> companyMasterOptional = companyMasterRepository.findById(companyId);
                    if (companyMasterOptional.isPresent()) {
                        companyMaster = companyMasterOptional.get();
                    }
                }
                em.setCompany(companyMaster);

                Long branchId = employeeExcelDto.getBranch();
                CompanyBranchMaster companyBranchMaster = null;

                if (branchId != null) {
                    Optional<CompanyBranchMaster> companyBranchMasterOptional = companyBranchMasterRepository.findById(branchId);
                    if (companyBranchMasterOptional.isPresent()) {
                        companyBranchMaster = companyBranchMasterOptional.get();
                    }
                }
                em.setCompanyBranch(companyBranchMaster);


                em.setIsGazeted(employeeExcelDto.isGazeted());
                em.setUnit(employeeExcelDto.getUnit());
                em.setPanNumber(employeeExcelDto.getPan_number());
                em.setEmployeeEligibleFor(employeeExcelDto.getEmployee_eligible_for());
                em.setAppId(employeeExcelDto.getApp_id());
                Employee ems = employeeRepository.save(em);
                if(ems != null) {
                    responses.setStatus("SUCCESS");
                    savedEmpId = ems.getId();
                }else {
                    responses.setStatus("FAIL");
                }

                List<Employee> e = employeeRepository.findbycodepostfix(employeeExcelDto.getEmp_code_postfix(), companyId, branchId);
                System.out.println("employee-----------------------------"+e);
                employeeExcelDto.setId(e.get(0).getId());
            }
            for (PersonalExcelDto personalExcelDto : personalExcelDtos) {
                EmpPersonalInfo pi = new EmpPersonalInfo();
                pi.setAddiInfo(personalExcelDto.getAddi_info());
                pi.setBankAccNo(personalExcelDto.getBank_acc_no());
                pi.setBirthPlace(personalExcelDto.getBirth_place());
                pi.setBsrCode(personalExcelDto.getBsr_code());
                pi.setCategory(personalExcelDto.getCategory());
                pi.setCommCategoryRef(personalExcelDto.getComm_category_ref());
                Date DateOfBirth = dateFormatter.parse(personalExcelDto.getDate_of_birth());
                pi.setDateOfBirth(DateOfBirth);
//	        	Date DateoFExpiry = dateFormatter.parse(personalExcelDto.getDate_of_expiry());
//	        	pi.setDateOfExpiry(DateoFExpiry);
                try {
                    Date dateOfExpiry = dateFormatter.parse(personalExcelDto.getDate_of_expiry());
                    pi.setDateOfExpiry(dateOfExpiry);
                } catch (ParseException e) {
                    pi.setDateOfExpiry(null);
                }
//	        	Date DateoFMarriage = dateFormatter.parse(personalExcelDto.getDate_of_marriage());
//	        	pi.setDateOfMarriage(DateoFMarriage);
                try {
                    Date DateoFMarriage = dateFormatter.parse(personalExcelDto.getDate_of_marriage());
                    pi.setDateOfMarriage(DateoFMarriage);
                }
                catch(ParseException e) {
                    pi.setDateOfMarriage(null);
                }

                pi.setDrivingLicenseNo(personalExcelDto.getDriving_license_no());
                pi.setGender(personalExcelDto.getGender());
                pi.setGovtVehicle(personalExcelDto.getGovt_vehicle_provided());
                pi.setGpfNo(personalExcelDto.getGpf_no());
                pi.setHobbies(personalExcelDto.getHobbies());
                pi.setIfscCode(personalExcelDto.getIfsc_code());
                pi.setIsAnyDisciplinaryProceding(personalExcelDto.getIs_any_disciplinary_proceedigns());
                pi.setIsResidentOtherCountry(personalExcelDto.getIs_resident_other_country());
                pi.setLicenseIssuedFor(personalExcelDto.getLicense_issued_for());
                pi.setMarriageStatus(personalExcelDto.getMarriage_status());
                pi.setPassPortNo(personalExcelDto.getPassport_no());
                pi.setPliNo(personalExcelDto.getPli_no());
                pi.setUidNo(personalExcelDto.getUid_no());
//	        	Date validUpto = dateFormatter.parse(personalExcelDto.getValid_upto());
//	        	pi.setValidUpto(validUpto);
                try {
                    Date validUpto = dateFormatter.parse(personalExcelDto.getValid_upto());
                    pi.setValidUpto(validUpto);
                }
                catch(ParseException e) {
                    pi.setValidUpto(null);
                }
                pi.setVisaDetail(personalExcelDto.getVisa_detail());

                BankMaster bankMaster = new BankMaster();
                bankMaster.setId(Long.parseLong(personalExcelDto.getBank_id()));
                pi.setBank(bankMaster);

                BankBranchMaster bankbranchMaster = new BankBranchMaster();
                bankbranchMaster.setId(Long.parseLong(personalExcelDto.getBank_branch_id()));
                pi.setBankBranch(bankbranchMaster);

                if(personalExcelDto.getCast_id()!=null) {
                    CastMaster castMaster = new CastMaster();
                    castMaster.setId(Long.parseLong(personalExcelDto.getCast_id()));
                    pi.setCast(castMaster);
                }

                if(personalExcelDto.getCountry_id()!=null) {
                    CountryMaster countryMaster = new CountryMaster();
                    countryMaster.setId(Long.parseLong(personalExcelDto.getCountry_id()));
                    pi.setCountry(countryMaster);
                }

                if(personalExcelDto.getReligion_id()!=null) {
                    ReligionMaster religionMaster = new ReligionMaster();
                    religionMaster.setId(Long.parseLong(personalExcelDto.getReligion_id()));
                    pi.setReligion(religionMaster);
                }

                if(personalExcelDto.getState_id()!=null){
                    StateMaster stateMaster = new StateMaster();
                    stateMaster.setId(Long.parseLong(personalExcelDto.getState_id()));
                    pi.setState(stateMaster);
                }
//	            Employee e = new Employee();
//	            e.setId(savedEmpId);
//	            pi.setEmp(e);

                Long employeeIdForPersonalInfo = findEmployeeIdByCodePostfix(personalExcelDto.getEmployee_code(), employeeExcelDtos);
                System.out.println("id for personal info"+employeeIdForPersonalInfo);
                if (employeeIdForPersonalInfo != null) {
                    // Set the found employee ID in EmpPersonalInfo
                    Employee matchingEmployeeForPersonalInfo = new Employee();
                    matchingEmployeeForPersonalInfo.setId(employeeIdForPersonalInfo);
                    pi.setEmp(matchingEmployeeForPersonalInfo);

                    // Save EmpPersonalInfo
                    EmpPersonalInfo epi = empPersonalInfoRepository.save(pi);
                    if (epi != null) {
                        responses.setStatus("SUCCESS");
                    } else {
                        responses.setStatus("FAIL");
                    }
                }

                pi.setDtlGovtVehicle(personalExcelDto.getDtl_govt_vehicle_provided());

//	        	try{
//	        		Date dateOfMigration = dateFormatter.parse(personalExcelDto.getDate_of_migration());
//	        		pi.setDateOfMigration(dateOfMigration);
//	        	}catch(ParseException e){
//	        		pi.setDateOfMigration(null);
//	        	}

                pi.setIsResidentOtherCountry(personalExcelDto.getIs_resident_other_country());
                pi.setIsAnyDisciplinaryProceding(personalExcelDto.getIs_any_disciplinary_proceedigns());

                EmpPersonalInfo epi = empPersonalInfoRepository.save(pi);
                if(epi != null) {
                    responses.setStatus("SUCCESS");
                }else {
                    responses.setStatus("FAIL");
                }

            }
            for (ContactExcelDto contactExcelDto : contactExcelDtos) {
                EmpContactDtl cd = new EmpContactDtl();
                cd.setCorporateEmail(contactExcelDto.getCorporate_email());
                cd.setExtension(contactExcelDto.getExtension());
                cd.setHomeMobile(contactExcelDto.getHome_mobile());
                cd.setHomePhone(contactExcelDto.getHome_phone());
                cd.setPersonalEmail(contactExcelDto.getPersonal_email());
                cd.setSecondaryEmail(contactExcelDto.getSecondary_email());
                cd.setWorkMobile(contactExcelDto.getWork_mobile());
                cd.setWorkPhone(contactExcelDto.getWork_phone());

                Long employeeIdForContact = findEmployeeIdByCodePostfix(contactExcelDto.getEmployee_code(), employeeExcelDtos);
                System.out.println("id for contact"+employeeIdForContact);
                if (employeeIdForContact != null) {
                    // Set the found employee ID in EmpContactDtl
                    Employee matchingEmployeeForContact = new Employee();
                    matchingEmployeeForContact.setId(employeeIdForContact);
                    cd.setEmp(matchingEmployeeForContact);
                }

                // Save EmpContactDtl
                EmpContactDtl epi = empContactRepository.save(cd);
                if (epi != null) {
                    responses.setStatus("SUCCESS");
                } else {
                    responses.setStatus("FAIL");
                }
            }
            for (FamilyExcelDto familyExcelDto : familyExcelDtos) {
                EmpFamilyDtl fd = new EmpFamilyDtl();
                fd.setCreatedBy(familyExcelDto.getCreated_by());
                fd.setAddress(familyExcelDto.getAddress());
                fd.setContactDetail(familyExcelDto.getContact_detail());
//	        		Date bithdate  =dateFormatter.parse(familyExcelDto.getDate_of_birth());
//	        		fd.setDateOfBirthday(bithdate);
                try {
                    Date bithdate  =dateFormatter.parse(familyExcelDto.getDate_of_birth());
                    fd.setDateOfBirthday(bithdate);
                }
                catch(ParseException e) {
                    fd.setDateOfBirthday(null);
                }
                fd.setFirstNameFamily(familyExcelDto.getFirst_name());
                fd.setGenderId(familyExcelDto.getGender());
                fd.setDelete(familyExcelDto.isDelete());
                fd.setDependent(familyExcelDto.isDependent());
                fd.setNominee(familyExcelDto.isNominee());
                fd.setPhysicallyDisabled(familyExcelDto.isPhysically_disabled());
                fd.setResidingWith(familyExcelDto.isResiding_with());
                fd.setLastNameFamily(familyExcelDto.getLast_name());
                fd.setCanbeContactedinEmergency(familyExcelDto.isCan_be_contacted_in_emergency());
                fd.setMaritalStatus(familyExcelDto.getMarital_status());
                fd.setMidddleNameFamily(familyExcelDto.getMiddle_name());
                fd.setOccupation(familyExcelDto.getOccupation());
                Long companyId = familyExcelDto.getCom_id();
                CompanyMaster companyMaster = null;
                if (companyId != null) {
                    Optional<CompanyMaster> companyMasterOptional = companyMasterRepository.findById(companyId);
                    if (companyMasterOptional.isPresent()) {
                        companyMaster = companyMasterOptional.get();
                    }
                }
                fd.setCompany(companyMaster);

                Long branchId = familyExcelDto.getBranch_id();
                CompanyBranchMaster companyBranchMaster = null;

                if (branchId != null) {
                    Optional<CompanyBranchMaster> companyBranchMasterOptional = companyBranchMasterRepository.findById(branchId);
                    if (companyBranchMasterOptional.isPresent()) {
                        companyBranchMaster = companyBranchMasterOptional.get();
                    }
                }
                fd.setCompanyBranch(companyBranchMaster);

                Long relationId=Long.parseLong(familyExcelDto.getRelation());
                FamilyRelationMaster familyRelationMaster=null;
                if (relationId != null) {
                    Optional<FamilyRelationMaster> FamilyRelationMasterOptional = familyRelationMasterRepository.findById(relationId);
                    if (FamilyRelationMasterOptional.isPresent()) {
                        familyRelationMaster = FamilyRelationMasterOptional.get();
                    }
                }
                fd.setFamilyRelationId(familyRelationMaster);

                Long employeeIdForfamily = findEmployeeIdByCodePostfix(familyExcelDto.getEmployee_code(), employeeExcelDtos);
                System.out.println("id for family"+employeeIdForfamily);
                if (employeeIdForfamily != null) {
                    // Set the found employee ID in EmpContactDtl
                    Employee matchingEmployeeForContact = new Employee();
                    matchingEmployeeForContact.setId(employeeIdForfamily);
                    fd.setEmp(matchingEmployeeForContact);
                }


                CountryMaster countryMaster=null;
                if (familyExcelDto.getNationality() != null) {
                    Long nationalityId=Long.parseLong(familyExcelDto.getNationality());
                    Optional<CountryMaster> CountryMasterOptional = countryMasterRepository.findById(nationalityId);
                    if (CountryMasterOptional.isPresent()) {
                        countryMaster = CountryMasterOptional.get();
                    }
                }
                fd.setNationality(countryMaster);
                EmpFamilyDtl epi = empFamilyRepository.save(fd);
                if(epi != null) {
                    responses.setStatus("SUCCESS");
                }else {
                    responses.setStatus("FAIL");
                }

            }

            for (AddressExcelDto addressExcelDto : addressExcelDtos) {
                EmpAddressDtl ad = new EmpAddressDtl();
                ad.setAddType(addressExcelDto.getAddress_type());
                ad.setAddressEmp(addressExcelDto.getAddress());
                ad.setAllAddSame(addressExcelDto.getAll_address_same() ? "Y" : "N");
                ad.setPincode(addressExcelDto.getPincode());
                ad.setPropertyType(addressExcelDto.getProperty_type());

                Long cityId =Long.parseLong(addressExcelDto.getCity());
                CityMaster cityMaster=null;
                if (cityId != null) {
                    Optional<CityMaster> CityMasterOptional = cityMasterRepository.findById(cityId);
                    if (CityMasterOptional.isPresent()) {
                        cityMaster = CityMasterOptional.get();
                    }
                }
                ad.setCity(cityMaster);

                Long countryId =Long.parseLong(addressExcelDto.getCountry());
                CountryMaster countryMaster=null;
                if (countryId != null) {
                    Optional<CountryMaster> CountryMasterOptional = countryMasterRepository.findById(countryId);
                    if (CountryMasterOptional.isPresent()) {
                        countryMaster = CountryMasterOptional.get();
                    }
                }
                ad.setCountry(countryMaster);

                Long DistrictId =Long.parseLong(addressExcelDto.getDistrict());
                DistrictMaster districtMaster=null;
                if (DistrictId != null) {
                    Optional<DistrictMaster> DistrictMasterOptional = districtMasterRepository.findById(DistrictId);
                    if (DistrictMasterOptional.isPresent()) {
                        districtMaster = DistrictMasterOptional.get();
                    }
                }
                ad.setDistrict(districtMaster);

                Long employeeIdForAddress = findEmployeeIdByCodePostfix(addressExcelDto.getEmployee_code(), employeeExcelDtos);
                System.out.println("id for family"+employeeIdForAddress);
                if (employeeIdForAddress != null) {
                    // Set the found employee ID in EmpContactDtl
                    Employee matchingEmployeeForContact = new Employee();
                    matchingEmployeeForContact.setId(employeeIdForAddress);
                    ad.setEmp(matchingEmployeeForContact);
                }

                Long StateId =Long.parseLong(addressExcelDto.getState());
                StateMaster stateMaster=null;
                if (StateId != null) {
                    Optional<StateMaster> StateMasterOptional = stateMasterRepositry.findById(StateId);
                    if (StateMasterOptional.isPresent()) {
                        stateMaster = StateMasterOptional.get();
                    }
                }
                ad.setState(stateMaster);

                EmpAddressDtl epi = empAddressRepository.save(ad);
                if(epi != null) {
                    responses.setStatus("SUCCESS");
                }else {
                    responses.setStatus("FAIL");
                }
            }

            for (EmergencyExcelDto emergencyExcelDto : emergencyExcelDtos) {
                EmpEmergencyDtl emd = new EmpEmergencyDtl();
                emd.setAddressEmg(emergencyExcelDto.getAddress());
                emd.setEmailEmg(emergencyExcelDto.getEmail());
                emd.setFirstNameEmg(emergencyExcelDto.getFirst_name());
                emd.setMiddleNameEmg(emergencyExcelDto.getMiddle_name());
                emd.setLastNameEmg(emergencyExcelDto.getLast_name());
                emd.setMobileNoEmg(emergencyExcelDto.getMobile_no());
                emd.setPhoneNoEmg(emergencyExcelDto.getPhone_no());
                emd.setPriority(emergencyExcelDto.getPriority());

                Long employeeIdForEmergency = findEmployeeIdByCodePostfix(emergencyExcelDto.getEmployee_code(), employeeExcelDtos);
                System.out.println("id for family"+employeeIdForEmergency);
                if (employeeIdForEmergency != null) {
                    // Set the found employee ID in EmpContactDtl
                    Employee matchingEmployeeForContact = new Employee();
                    matchingEmployeeForContact.setId(employeeIdForEmergency);
                    emd.setEmp(matchingEmployeeForContact);
                }

                Long RelationId =Long.parseLong(emergencyExcelDto.getRelation());
                FamilyRelationMaster familyRelationMaster=null;
                if (RelationId != null) {
                    Optional<FamilyRelationMaster> FamilyRelationMasterOptional = familyRelationMasterRepository.findById(RelationId);
                    if (FamilyRelationMasterOptional.isPresent()) {
                        familyRelationMaster = FamilyRelationMasterOptional.get();
                    }
                }
                emd.setFamilyRelationEmg(familyRelationMaster);

                List<EmpFamilyDtl> efd = empFamilyRepository.findByEmpId(emd.getEmp().getId());
                System.out.println("------------------------>>>"+efd);
                if (efd != null && !efd.isEmpty()) {
                    emd.setEmpFamily(efd.get(0));
                }

                EmpEmergencyDtl epi = empEmergencyRepository.save(emd);
                if(epi != null) {
                    responses.setStatus("SUCCESS");
                }else {
                    responses.setStatus("FAIL");
                }
            }
            for (NomineeExcelDto nomineeExcelDto : nomineeExcelDtos) {
                Nominee nm = new Nominee();
                nm.setPriority(Long.parseLong(nomineeExcelDto.getPriority()));
                nm.setNomineeFirstName(nomineeExcelDto.getFirst_name());
                nm.setNomineeMiddleName(nomineeExcelDto.getMiddle_name());
                nm.setNomineeLastName(nomineeExcelDto.getLast_name());

                Long RelationId =Long.parseLong(nomineeExcelDto.getRelation());
                FamilyRelationMaster familyRelationMaster=null;
                if (RelationId != null) {
                    Optional<FamilyRelationMaster> FamilyRelationMasterOptional = familyRelationMasterRepository.findById(RelationId);
                    if (FamilyRelationMasterOptional.isPresent()) {
                        familyRelationMaster = FamilyRelationMasterOptional.get();
                    }
                }
                nm.setFamilyRelationMaster(familyRelationMaster);
                nm.setGender(nomineeExcelDto.getGender());
//		        	Date DateOfBirth = dateFormatter.parse(nomineeExcelDto.getDate_of_birth());
//		        	nm.setDateOfBirth(DateOfBirth);
                try {
                    Date DateOfBirth = dateFormatter.parse(nomineeExcelDto.getDate_of_birth());
                    nm.setDateOfBirth(DateOfBirth);
                }
                catch(ParseException e) {
                    nm.setDateOfBirth(null);
                }
                nm.setUidNo(nomineeExcelDto.getUid_no());
                nm.setPanNumber(nomineeExcelDto.getPan_no());
                nm.setContactNumber(nomineeExcelDto.getContact_no());
                nm.setNomineeInvalidCondition(nomineeExcelDto.getNominee_invalid_condition());

                Long employeeIdForNominee = findEmployeeIdByCodePostfix(nomineeExcelDto.getEmployee_code(), employeeExcelDtos);
                //System.out.println("i"+employeeIdForNominee);
                if (employeeIdForNominee != null) {
                    // Set the found employee ID in EmpContactDtl
                    Employee matchingEmployeeForContact = new Employee();
                    matchingEmployeeForContact.setId(employeeIdForNominee);
                    nm.setEmp(matchingEmployeeForContact);
                }

                Long companyId = nomineeExcelDto.getCom_id();
                CompanyMaster companyMaster = null;
                if (companyId != null) {
                    Optional<CompanyMaster> companyMasterOptional = companyMasterRepository.findById(companyId);
                    if (companyMasterOptional.isPresent()) {
                        companyMaster = companyMasterOptional.get();
                    }
                }
                nm.setCompany(companyMaster);

                Long branchId = nomineeExcelDto.getBranch_id();
                CompanyBranchMaster companyBranchMaster = null;

                if (branchId != null) {
                    Optional<CompanyBranchMaster> companyBranchMasterOptional = companyBranchMasterRepository.findById(branchId);
                    if (companyBranchMasterOptional.isPresent()) {
                        companyBranchMaster = companyBranchMasterOptional.get();
                    }
                }
                nm.setCompanyBranch(companyBranchMaster);

                List<EmpFamilyDtl> efd = empFamilyRepository.findByEmpId(nm.getEmp().getId());
                if (efd != null && !efd.isEmpty()) {
                    nm.setEmpFamily(efd.get(0));
                }

                Nominee epi = nomineeRepo.save(nm);
                if(epi != null) {
                    responses.setStatus("SUCCESS");
                }else {
                    responses.setStatus("FAIL");
                }
            }

            for (JobExcelDto jobExcelDto : JobExcelDtos) {
                Job j = new Job();
//	        		Date cnfrmDate = dateFormatter.parse(jobExcelDto.getConfirmation_date());
//	        		j.setConfirmationDate(cnfrmDate);
                j.setDutiesAndResponsibility(jobExcelDto.getDuties_responsibility());
//	        		Date insuranceEffectDate = dateFormatter.parse(jobExcelDto.getInsurance_effective_from());
//	        		j.setInsuranceWithEffectiveFrom(insuranceEffectDate);
                try {
                    Date insuranceEffectDate = dateFormatter.parse(jobExcelDto.getInsurance_effective_from());
                    j.setInsuranceWithEffectiveFrom(insuranceEffectDate);
                }
                catch(ParseException e){
                    j.setInsuranceWithEffectiveFrom(null);
                }
//	        		j.setNoticePeriod(Long.parseLong(jobExcelDto.getNotice_period()));
                String noticePeriodStr = jobExcelDto.getNotice_period();
                Long noticePeriod;

                if (noticePeriodStr != null && !noticePeriodStr.isEmpty()) {
                    try {
                        noticePeriod = Long.parseLong(noticePeriodStr);
                        j.setNoticePeriod(noticePeriod);
                    } catch (NumberFormatException e) {
                        j.setNoticePeriod(null);
                    }
                } else {
                    j.setNoticePeriod(null);
                }
//	        		Date postingDate = dateFormatter.parse(jobExcelDto.getPosting_date());
//	        		j.setPostingDate(postingDate);
                try {
                    Date postingDate = dateFormatter.parse(jobExcelDto.getPosting_date());
                    j.setPostingDate(postingDate);
                }
                catch(ParseException e) {
                    j.setPostingDate(null);
                }
//	        		Date postingOrderDate = dateFormatter.parse(jobExcelDto.getPosting_order_date());
//	        		j.setPostingOrderDate(postingOrderDate);
                try {
                    Date postingOrderDate = dateFormatter.parse(jobExcelDto.getPosting_order_date());
                    j.setPostingOrderDate(postingOrderDate);
                }
                catch(ParseException e) {
                    j.setPostingOrderDate(null);
                }
//	        		j.setPostingOrderNumber(Long.parseLong(jobExcelDto.getPosting_order_no()));
                String postingOrderNoStr = jobExcelDto.getPosting_order_no();
                Long postingOrderNo;

                if (postingOrderNoStr != null && !postingOrderNoStr.isEmpty()) {
                    try {
                        postingOrderNo = Long.parseLong(postingOrderNoStr);
                        j.setPostingOrderNumber(postingOrderNo);
                    } catch (NumberFormatException e) {
                        j.setPostingOrderNumber(null);
                    }
                } else {

                    j.setPostingOrderNumber(null);
                }

//	        		Date retirementDate = dateFormatter.parse(jobExcelDto.getRetirement_date());
//	        		j.setRetirementDate(retirementDate);
                try {
                    Date retirementDate = dateFormatter.parse(jobExcelDto.getRetirement_date());
                    j.setRetirementDate(retirementDate);
                }
                catch(ParseException e) {
                    j.setRetirementDate(null);
                }
//	        		Date setWeeklyOffWithEffectiveFrom = dateFormatter.parse(jobExcelDto.getWeekly_off_effective_from());
//	        		j.setWeeklyOffWithEffectiveFrom(setWeeklyOffWithEffectiveFrom);
                try {
                    Date setWeeklyOffWithEffectiveFrom = dateFormatter.parse(jobExcelDto.getWeekly_off_effective_from());
                    j.setWeeklyOffWithEffectiveFrom(setWeeklyOffWithEffectiveFrom);
                }
                catch(ParseException e) {
                    j.setWeeklyOffWithEffectiveFrom(null);
                }

                Long companyId = Long.parseLong(jobExcelDto.getCom_id());
                CompanyMaster companyMaster = null;
                if (companyId != null) {
                    Optional<CompanyMaster> companyMasterOptional = companyMasterRepository.findById(companyId);
                    if (companyMasterOptional.isPresent()) {
                        companyMaster = companyMasterOptional.get();
                    }
                }
                j.setCompany(companyMaster);

                Long branchId = jobExcelDto.getBranch_id();
                CompanyBranchMaster companyBranchMaster = null;

                if (branchId != null) {
                    Optional<CompanyBranchMaster> companyBranchMasterOptional = companyBranchMasterRepository.findById(branchId);
                    if (companyBranchMasterOptional.isPresent()) {
                        companyBranchMaster = companyBranchMasterOptional.get();
                    }
                }
                j.setCompanyBranch(companyBranchMaster);

                Long employeeIdForContact = findEmployeeIdByCodePostfix(jobExcelDto.getEmployee_code(), employeeExcelDtos);
                System.out.println("id for Job"+employeeIdForContact);
                if (employeeIdForContact != null) {
                    // Set the found employee ID in EmpContactDtl
                    Employee matchingEmployeeForContact = new Employee();
                    matchingEmployeeForContact.setId(employeeIdForContact);
                    j.setEmp(matchingEmployeeForContact);
                }


                DesignationMaster designationMaster = null;

                if (jobExcelDto.getDestination_id() != null) {
                    Long designationId = Long.parseLong(jobExcelDto.getDestination_id());
                    Optional<DesignationMaster> DesignationMasterOptional = designationMasterRepository.findById(designationId);
                    if (DesignationMasterOptional.isPresent()) {
                        designationMaster = DesignationMasterOptional.get();
                    }
                }
                j.setDesignation(designationMaster);


                EmployeementCategory employeementCategory = null;

                if (jobExcelDto.getEmployee_category_id() != null) {
                    Long empCatId = Long.parseLong(jobExcelDto.getEmployee_category_id());
                    Optional<EmployeementCategory> EmployeementCategoryOptional = employeeCategoryRepository.findById(empCatId);
                    if (EmployeementCategoryOptional.isPresent()) {
                        employeementCategory = EmployeementCategoryOptional.get();
                    }
                }
                j.setEmployeementCategory(employeementCategory);


                EmployeementType employeementType = null;

                if (jobExcelDto.getEmployement_type_id() != null) {
                    Long empType = Long.parseLong(jobExcelDto.getEmployement_type_id());
                    Optional<EmployeementType> EmployeementTypeOptional = employeementTypeRepository.findById(empType);
                    if (EmployeementTypeOptional.isPresent()) {
                        employeementType = EmployeementTypeOptional.get();
                    }
                }
                j.setEmployeementType(employeementType);

                if(jobExcelDto.getGroup_id()!=null) {
                    GroupMaster groupMaster = new GroupMaster();
                    groupMaster.setId(Long.parseLong(jobExcelDto.getGroup_id()));
                    j.setGroupId(groupMaster);
                }

                if(jobExcelDto.getGrade_id()!=null) {
                    GradeMaster gradeMaster = new GradeMaster();
                    gradeMaster.setId(Long.parseLong(jobExcelDto.getGrade_id()));
                    j.setGradeId(gradeMaster);
                }

                if(jobExcelDto.getGrade_pay_id()!=null) {
                    GradePayMaster gradePayMaster =new GradePayMaster();
                    gradePayMaster.setId(Long.parseLong(jobExcelDto.getGrade_pay_id()));
                    j.setGradePayId(gradePayMaster);
                }
//		            PayBandMaster payBandMaster = new PayBandMaster();
//		            payBandMaster.setId(jobExcelDto.getPay_band_id());
//		            j.setPayBand(payBandMaster);

                if(jobExcelDto.getPay_commission_id()!=null) {
                    PayCommissionMaster payCommissionMaster =new PayCommissionMaster();
                    payCommissionMaster.setId(Long.parseLong(jobExcelDto.getPay_commission_id()));
                    j.setPayCommissionId(payCommissionMaster);
                }

                if(jobExcelDto.getRecruitment_type_id()!=null) {
                    RecruitmentType recruitmentType = new RecruitmentType();
                    recruitmentType.setId(Long.parseLong(jobExcelDto.getRecruitment_type_id()));
                    j.setRecruitmentTypeId(recruitmentType);
                }

                if(jobExcelDto.getShift_id()!=null) {
                    ShiftPolicyMaster shiftPolicyMaster =new ShiftPolicyMaster();
                    shiftPolicyMaster.setId(Long.parseLong(jobExcelDto.getShift_id()));
                    j.setShiftpolicyMaster(shiftPolicyMaster);
                }

                if(jobExcelDto.getStatus_id()!=null) {
                    StatusMaster statusMaster = new StatusMaster();
                    statusMaster.setId(Long.parseLong(jobExcelDto.getStatus_id()));
                    j.setStatusId(statusMaster);
                }

                if(jobExcelDto.getWeekly_off()!=null) {
                    WeeklyOffPolicyMaster weeklyOffPolicyMaster = new WeeklyOffPolicyMaster();
                    weeklyOffPolicyMaster.setId(Long.parseLong(jobExcelDto.getWeekly_off()));
                    j.setWeeklyOff(weeklyOffPolicyMaster);
                }

                CompanyMaster company = companyMasterRepository.findById(companyyId).orElse(null);
                if (company != null) {
                    j.setCompany(company);
                } else {
                    throw new IllegalStateException("CompanyMaster with id " + companyyId + " not found in the database.");
                }

                CompanyBranchMaster companyBranch = companyBranchMasterRepository.findById(companyBranchId).orElse(null);
                if (companyBranch != null) {
                    j.setCompanyBranch(companyBranch);
                } else {
                    throw new IllegalStateException("CompanyBranchMaster with id " + companyBranchId + " not found in the database.");
                }

                Job epi = jobRepo.save(j);
                if(epi != null) {
                    responses.setStatus("SUCCESS");
                }else {
                    responses.setStatus("FAIL");
                }

            }


            for (HealthExcelDto healthExcelDto : HealthExcelDtos) {
                EmpHealthDtl h = new EmpHealthDtl();
                h.setHeight(Double.parseDouble(healthExcelDto.getHeight()));
//	        		h.setWeight(Double.parseDouble(healthExcelDto.getWeight()));
                String weightInput = healthExcelDto.getWeight();
                if (weightInput != null && !weightInput.isEmpty()) {
                    h.setWeight(Double.parseDouble(weightInput));
                } else {
                    h.setWeight(null);
                }
                h.setBloodGroup(healthExcelDto.getBlood_group());
                h.setIdentificationMarkFirst(healthExcelDto.getIdentification_mark_first());
                h.setIdentificationMarkSecond(healthExcelDto.getIdentification_mark_second());
                h.setPhysicallyHandicapped(Boolean.parseBoolean(healthExcelDto.getPhysically_disabled()));
                Long companyId = healthExcelDto.getCom_id();
                CompanyMaster companyMaster = null;
                if (companyId != null) {
                    Optional<CompanyMaster> companyMasterOptional = companyMasterRepository.findById(companyId);
                    if (companyMasterOptional.isPresent()) {
                        companyMaster = companyMasterOptional.get();
                    }
                }
                h.setCompany(companyMaster);

                Long branchId = healthExcelDto.getBranch_id();
                CompanyBranchMaster companyBranchMaster = null;

                if (branchId != null) {
                    Optional<CompanyBranchMaster> companyBranchMasterOptional = companyBranchMasterRepository.findById(branchId);
                    if (companyBranchMasterOptional.isPresent()) {
                        companyBranchMaster = companyBranchMasterOptional.get();
                    }
                }
                h.setCompanyBranch(companyBranchMaster);

                Long employeeIdForContact = findEmployeeIdByCodePostfix(healthExcelDto.getEmployee_code(), employeeExcelDtos);
                System.out.println("id for Job"+employeeIdForContact);
                if (employeeIdForContact != null) {
                    // Set the found employee ID in EmpContactDtl
                    Employee matchingEmployeeForContact = new Employee();
                    matchingEmployeeForContact.setId(employeeIdForContact);
                    h.setEmp(matchingEmployeeForContact);
                }
                h.setCreatedBy(healthExcelDto.getCreatedBy());

                EmpHealthDtl epi = empHealthRepository.save(h);
                if(epi != null) {
                    responses.setStatus("SUCCESS");
                }else {
                    responses.setStatus("FAIL");
                }

            }

            for (PreviousEmployementExcelDto previousEmployementExcelDto : previousEmployementExcelDtos) {
                EmpPreviousEmployment epe = new EmpPreviousEmployment();
                epe.setAddress(previousEmployementExcelDto.getAddress());
                epe.setCompanyName(previousEmployementExcelDto.getCompanyName());
                epe.setEmail(previousEmployementExcelDto.getEmail());
//	        		Date FromDate = dateFormatter.parse(previousEmployementExcelDto.getFrom_date());
//	        		epe.setFromDate(FromDate);
                try {
                    Date FromDate = dateFormatter.parse(previousEmployementExcelDto.getFrom_date());
                    epe.setFromDate(FromDate);
                }
                catch(ParseException e) {
                    epe.setFromDate(null);
                }
                epe.setHrContactPerson(previousEmployementExcelDto.getHr_contact_person());
                epe.setLastCTC(previousEmployementExcelDto.getLast_ctc());
                epe.setMobileNo(previousEmployementExcelDto.getMobile_no());
                epe.setPhoneNo(previousEmployementExcelDto.getPhone_no());
                epe.setPosition(previousEmployementExcelDto.getPosition());
                epe.setReasonForSeparation(previousEmployementExcelDto.getReason_for_separation());
                epe.setRemark(previousEmployementExcelDto.getRemark());
                epe.setResponsibilities(previousEmployementExcelDto.getResponsibilities());
//	        		epe.setServiceType(previousEmployementExcelDto.getService_type());
                String serviceType = previousEmployementExcelDto.getService_type();
                if (serviceType != null && !serviceType.isEmpty()) {
                    if (serviceType.equalsIgnoreCase("Full time")) {
                        epe.setServiceType("FT");
                    } else {
                        epe.setServiceType("PT");
                    }
                }
//	        		Date toDate = dateFormatter.parse(previousEmployementExcelDto.getTo_date());
//	        		epe.setToDate(toDate);
                try {
                    Date toDate = dateFormatter.parse(previousEmployementExcelDto.getTo_date());
                    epe.setToDate(toDate);
                }
                catch(ParseException e) {
                    epe.setToDate(null);
                }
                epe.setWebSiteUrl(previousEmployementExcelDto.getWebsite_url());

                Long employeeIdForpreviousEmployement = findEmployeeIdByCodePostfix(previousEmployementExcelDto.getEmployee_code(), employeeExcelDtos);
                System.out.println("id for Job"+employeeIdForpreviousEmployement);
                if (employeeIdForpreviousEmployement != null) {
                    // Set the found employee ID in EmpContactDtl
                    Employee matchingEmployeeForContact = new Employee();
                    matchingEmployeeForContact.setId(employeeIdForpreviousEmployement);
                    epe.setEmp(matchingEmployeeForContact);
                }
                EmpPreviousEmployment epi = empPreviousEmploymentRepository.save(epe);
                if(epi != null) {
                    responses.setStatus("SUCCESS");
                }else {
                    responses.setStatus("FAIL");
                }
            }

            for (EducationQualificationExcelDto educationQualificationExcelDto : educationQualificationExcelDtos) {
                EmpEducationQualificationDtl eq = new EmpEducationQualificationDtl();
                eq.setCreatedBy(um.getId());
                eq.setDuration(educationQualificationExcelDto.getDuration());
                eq.setGpaScore(educationQualificationExcelDto.getGpa_score());
                eq.setInstitute(educationQualificationExcelDto.getInstitute());
                eq.setInstituteAddress(educationQualificationExcelDto.getInstitute_address());
                eq.setMajor(educationQualificationExcelDto.getMajor());
                eq.setModeOfStudy(educationQualificationExcelDto.getMode_of_study());
                eq.setPassingOfMonth(educationQualificationExcelDto.getPassing_of_month());
                eq.setPassingYear(educationQualificationExcelDto.getPassing_year());
                eq.setPercentage(educationQualificationExcelDto.getPercentage());
                eq.setPercentile(educationQualificationExcelDto.getPercentile());
                eq.setRemark(educationQualificationExcelDto.getRemark());
                eq.setUniversity(educationQualificationExcelDto.getUniversity());

                Long companyId = educationQualificationExcelDto.getCom_id();
                CompanyMaster companyMaster = null;
                if (companyId != null) {
                    Optional<CompanyMaster> companyMasterOptional = companyMasterRepository.findById(companyId);
                    if (companyMasterOptional.isPresent()) {
                        companyMaster = companyMasterOptional.get();
                    }
                }
                eq.setCompany(companyMaster);

                Long branchId = educationQualificationExcelDto.getBranch_id();
                CompanyBranchMaster companyBranchMaster = null;

                if (branchId != null) {
                    Optional<CompanyBranchMaster> companyBranchMasterOptional = companyBranchMasterRepository.findById(branchId);
                    if (companyBranchMasterOptional.isPresent()) {
                        companyBranchMaster = companyBranchMasterOptional.get();
                    }
                }
                eq.setCompanyBranch(companyBranchMaster);

                Long employeeIdForContact = findEmployeeIdByCodePostfix(educationQualificationExcelDto.getEmployee_code(), employeeExcelDtos);
                System.out.println("id for Job"+employeeIdForContact);
                if (employeeIdForContact != null) {
                    // Set the found employee ID in EmpContactDtl
                    Employee matchingEmployeeForContact = new Employee();
                    matchingEmployeeForContact.setId(employeeIdForContact);
                    eq.setEmp(matchingEmployeeForContact);
                }
                eq.setGrade(educationQualificationExcelDto.getGrade());

                EducationMaster educationMaster =new EducationMaster();
                educationMaster.setId(Long.parseLong(educationQualificationExcelDto.getQualification()));
                eq.setQualification(educationMaster);

                EmpEducationQualificationDtl epi = empEducationQualificationDtlRepository.save(eq);
                if(epi != null) {
                    responses.setStatus("SUCCESS");
                }else {
                    responses.setStatus("FAIL");
                }
            }

            for (ReportingOfficeExcelDto reportingOfficeExcelDto : reportingOfficeExcelDtos) {
                EmpReportingOfficer ero = new EmpReportingOfficer();

//	        		Date authorizationDate = dateFormatter.parse(reportingOfficeExcelDto.getAuthorization_date());
//	        		ero.setAuthorizationDate(authorizationDate);
                try {
                    Date authorizationDate = dateFormatter.parse(reportingOfficeExcelDto.getAuthorization_date());
                    ero.setAuthorizationDate(authorizationDate);
                }
                catch(ParseException e) {
                    ero.setAuthorizationDate(null);
                }

//	        		Date endDate = dateFormatter.parse(reportingOfficeExcelDto.getEnd_date());
//	        		ero.setEndDate(endDate);
                try {
                    Date endDate = dateFormatter.parse(reportingOfficeExcelDto.getEnd_date());
                    ero.setEndDate(endDate);
                }
                catch(ParseException e) {
                    ero.setEndDate(null);
                }

                ero.setNote(reportingOfficeExcelDto.getNote());

//	        		Date startDate = dateFormatter.parse(reportingOfficeExcelDto.getStart_date());
//	        		ero.setStartDate(startDate);
                try {
                    Date startDate = dateFormatter.parse(reportingOfficeExcelDto.getStart_date());
                    ero.setStartDate(startDate);
                }
                catch(ParseException e) {
                    ero.setStartDate(null);
                }

//	        		ero.setDdo(reportingOfficeExcelDto.getDdo_id());
                if(reportingOfficeExcelDto.getDdo_id()!=null) {
                    Long ddoId = Long.parseLong(reportingOfficeExcelDto.getDdo_id());
                    Employee ddoEmployee = employeeRepository.findById(ddoId).orElse(null);
                    ero.setDdo(ddoEmployee);
                }

                Long dhId = Long.parseLong(reportingOfficeExcelDto.getDh_id());
                Employee dhEmployee = employeeRepository.findById(dhId).orElse(null);
                ero.setDh(dhEmployee);

                Long employeeIdForReportingOfficer = findEmployeeIdByCodePostfix(reportingOfficeExcelDto.getEmployee_code(), employeeExcelDtos);
                System.out.println("id for Job"+employeeIdForReportingOfficer);
                if (employeeIdForReportingOfficer != null) {
                    // Set the found employee ID
                    Employee matchingEmployeeForContact = new Employee();
                    matchingEmployeeForContact.setId(employeeIdForReportingOfficer);
                    ero.setEmp(matchingEmployeeForContact);
                }

                Long hoId = Long.parseLong(reportingOfficeExcelDto.getHo_id());
                Employee hoEmployee = employeeRepository.findById(hoId).orElse(null);
                ero.setHo(hoEmployee);

                Long hodId = Long.parseLong(reportingOfficeExcelDto.getHod_id());
                Employee hodEmployee = employeeRepository.findById(hodId).orElse(null);
                ero.setHod(hodEmployee);


                EmpReportingOfficer epi = empReportingOfficerRepository.save(ero);
                if(epi != null) {
                    responses.setStatus("SUCCESS");
                }else {
                    responses.setStatus("FAIL");
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return responses.getStatus();
    }

    private Long findEmployeeIdByCodePostfix(String empCodePostfix, List<EmployeeExcelDto> employeeExcelDtos) {
        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
            if (empCodePostfix.equals(employeeExcelDto.getEmp_code_postfix())) {
                return employeeExcelDto.getId(); // Assuming EmployeeExcelDto has a method getId()
            }
        }
        return null; // Return null if no matching employee is found
    }

    public static boolean isValidPANNumber(String input) {
        String validRegex = "([A-Z]){5}([0-9]){4}([A-Z]){1}$";
        Pattern pattern = Pattern.compile(validRegex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static boolean isValidMobileNumber(String input) {
        String validRegex = "\\d{10}";
        Pattern pattern = Pattern.compile(validRegex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
    public static boolean isValidPhoneNumber(String input) {
        String validRegex = "^[0]{1}[0-9]{10}$";
        Pattern pattern = Pattern.compile(validRegex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
    public static boolean isValidEmail(String input) {
        String validRegex = "^([a-zA-Z0-9_\\.-])+@(([a-zA-Z0-9-])+)\\.([a-zA-Z0-9]{2,4})+$";
        Pattern pattern = Pattern.compile(validRegex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static boolean validateAppointmentDate(String dateOfAppointment, String dateOfBirth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date appointDate = dateFormat.parse(dateOfAppointment);
            Date birthDate = dateFormat.parse(dateOfBirth);

            if (appointDate == null || birthDate == null) {
                return true;
            } else {
                if (appointDate.before(birthDate)) {
                    int years = Math.abs(getYearDifference(birthDate, appointDate));
                    if (years < 18) {
                        // Handle error - Minimum 18 years gap between Date of Appointment and Date of Birth
                        return false;
                    } else {
                        // Clear any previous errors
                        // removeClsAndHtml('dateOfAppointment', 'dateOfAppointmentError', 'appCls', '', 'textError');
                        return true;
                    }
                } else if (birthDate.before(appointDate)) {
                    int years = Math.abs(getYearDifference(birthDate, appointDate));
                    if (years < 18) {
                        // Handle error - Minimum 18 years gap between Date of Appointment and Date of Birth
                        return false;
                    } else {
                        // Clear any previous errors
                        // removeClsAndHtml('dateOfAppointment', 'dateOfAppointmentError', 'appCls', '', 'textError');
                        return true;
                    }
                }
            }
        } catch (ParseException e) {
            // Handle parsing exception
            e.printStackTrace();
        }

        return true;
    }

    private static int getYearDifference(Date fromDate, Date toDate) {
        int fromYear = fromDate.getYear();
        int toYear = toDate.getYear();

        return toYear - fromYear;
    }

    private EmployeeExcelDto findEmployeeByCode(String employeeCode, List<EmployeeExcelDto> employeeExcelDtos) {
        for (EmployeeExcelDto employeeExcelDto : employeeExcelDtos) {
            if (employeeExcelDto.getEmp_code_postfix().equals(employeeCode)) {
                return employeeExcelDto;
            }
        }
        return null;
    }
    private boolean isValidACNumber(String input) {
        // Your validation logic here
        // Using regex pattern for validation
        String validRegex = "^\\d{9,18}$";
        return Pattern.matches(validRegex, input);
    }

    private boolean isValidIFSC(String input) {
        input = input.trim();
        String validRegex = "^[A-Z]{4}0[A-Z0-9]{6}$";
        Pattern pattern = Pattern.compile(validRegex);
        Matcher matcher = pattern.matcher(input);
        return Pattern.matches(validRegex, input);
    }

    private boolean isValidBSR(String input) {
        String validRegex = "^\\d{7}$";
        // return Pattern.matches(validRegex, input);
        Pattern pattern = Pattern.compile(validRegex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    private boolean isValidPassportNo(String input) {
        String validRegex = "^[A-PR-WY-Z][1-9]\\d\\s?\\d{4}[1-9]$";
        Pattern pattern = Pattern.compile(validRegex);
        Matcher matcher = pattern.matcher(input);
        return Pattern.matches(validRegex, input);
    }

    @GetMapping("/maritalStatus/{empId}")
    @ResponseBody
    public ResponseEntity<String> getMaritalStatus(@PathVariable Long empId) {

        System.out.println("Fetching marital status for empId: " + empId);
        String maritalStatus = empPersonalInfoRepository.findMaritalStatusByEmpId(empId);
        System.out.println("Data is " + maritalStatus);


        switch (maritalStatus) {
            case "M":
                maritalStatus = "Married";
                break;
            case "U":
                maritalStatus = "Unmarried";
                break;
            default:
                maritalStatus = "Unknown";
        }
        return ResponseEntity.ok(maritalStatus);
    }



}
