package com.sttl.hrms.HR.empmgmt.empinfo.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sttl.hrms.HR.empmgmt.empinfo.model.Employee;
import com.sttl.hrms.masters.model.DesignationMaster;
import com.sttl.hrms.masters.model.UserMaster;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,DataTablesRepository<Employee, Long> {

	@Query(value = "select count(*)  from employeemanagement.employee e "
			+ "join employeemanagement.designation_master dm on dm.id = e.designation_id "
			+ "where dm.designation_name =:designationName and e.com_id =:companyId and e.branch_id =:companyBranchId", nativeQuery = true)
	Long getPresentPosition(@Param("designationName") String designationName, @Param("companyId") Long companyId,
							@Param("companyBranchId") Long companyBranchId);

	@Query(value = "SELECT e.* from " + " employeemanagement.employee  e"
			+ " inner join employeemanagement.user_master um ON um.emp_id = e.id"
			+ " where lower(e.first_name) like lower(:firstName) "
			+ " AND e.com_id =:companyId AND e.branch_id =:companyBranchId AND um.user_id ~ '^IR[0-9]{6}$'", nativeQuery = true)
	List<Employee> findAllByIsDeleteFalse(@Param("firstName") String firstName, @Param("companyId") Long companyId,
										  @Param("companyBranchId") Long companyBranchId);

	Employee findAllByempCode(String empCode);

	// @Query(value = "SELECT * from employeemanagement.employee where
	// lower(first_name) like lower(:firstName) AND com_id =:companyId AND branch_id
	// =:companyBranchId", nativeQuery = true)

	@Query(value = "SELECT DISTINCT ON (e.id) e.* from " + " employeemanagement.employee  e"
			+ " inner join employeemanagement.user_master um ON um.emp_id = e.id "
			+  "inner join employeemanagement.job j on j.emp_id=e.id "
			+ " where lower(e.first_name) like lower(:firstName) " + " AND um.system_role_type = :empListFor"
			+ " AND e.com_id =:companyId AND e.branch_id =:companyBranchId AND e.is_delete = false and j.status_id != 2", nativeQuery = true)
	List<Employee> findLikeEmployeeByCompanyIdAndCompnayBranchIdAndIsDeleteFalse(@Param("firstName") String firstName,
																				 @Param("empListFor") String empListFor, @Param("companyId") Long companyId,
																				 @Param("companyBranchId") Long companyBranchId);

	// @Query(value = "select max(emp_code_postfix) from employeemanagement.employee
	// where com_id =:companyId and branch_id =:companyBranchId and
	// (lower(emp_code_prefix)=lower(:empCodePrefix) or '0'=lower(:empCodePrefix))",
	// nativeQuery = true)
	@Query(value = "select max(emp_code_postfix) from employeemanagement.employee where com_id =:companyId and (lower(emp_code_prefix)=lower(:empCodePrefix) or '0'=lower(:empCodePrefix))", nativeQuery = true)
	String findMaxEmployeeCodeByPrefix(@Param("companyId") Long companyId,
									   @Param("empCodePrefix") String empCodePrefix);



//	Employee findByEmpCodeIgnoreCaseAndIsDeleteFalseAndCompanyIdAndCompanyBranchId(String empCode, Long companyId,
//			Long companyBranchId);

	Employee findByEmpCodeIgnoreCaseAndIsDeleteFalseAndCompanyId(String empCode, Long companyId);

//	@Query(value = "select * from employeemanagement.employee where  id!=?1 AND lower(emp_code)=lower(?2) AND is_delete = false AND com_id=?3 AND branch_id=?4 ",nativeQuery = true)
//	Employee findByIdAndEmpCodeIgnoreCaseAndIsDeleteFalseAndCompanyIdAndCompanyBranchId(Long id, String empCode,
//			Long companyId, Long companyBranchId);

	@Query(value = "select * from employeemanagement.employee where  id!=?1 AND lower(emp_code)=lower(?2) AND is_delete = false AND com_id=?3", nativeQuery = true)
	Employee findByIdAndEmpCodeIgnoreCaseAndIsDeleteFalseAndCompanyId(Long id, String empCode, Long companyId);


//	@Query(value = "SELECT CASE WHEN count(*) > 0 THEN true ELSE false END FROM employeemanagement.employee WHERE lower(emp_code) = lower(?1) AND is_delete = false", nativeQuery = true)
//	boolean existsByEmpCodeIgnoreCase(String empCodePrefix);

//	@Query(value = "SELECT CASE WHEN count(*) > 0 THEN true ELSE false END FROM employeemanagement.employee WHERE lower(emp_code) = lower(?1) AND com_id = :companyId AND is_delete = false", nativeQuery = true)
//	boolean existsByEmpCodeIgnoreCaseAndComId(String empCodePrefix, Long companyId);

//	@Query(value = "SELECT CASE WHEN count(*) > 0 THEN true ELSE false END FROM employeemanagement.employee WHERE lower(emp_code) = lower(?1) AND com_id = ?2 AND is_delete = false", nativeQuery = true)
//	boolean existsByEmpCodeIgnoreCaseAndComId(String empCodePrefix, Long companyId);

	@Query(value = "SELECT CASE WHEN count(*) > 0 THEN true ELSE false END FROM employeemanagement.employee WHERE (lower(emp_code) = lower(?1) OR pan_number = ?2) AND com_id = ?3 AND is_delete = false", nativeQuery = true)
	boolean existsByEmpCodeIgnoreCaseOrPanNumberAndComId(String empCodePrefix, String panNumber, Long companyId);




	@Query(value = "select e.* from employeemanagement.employee e "
			+ "inner join employeemanagement.designation_master dm on dm.id = e.designation_id "
			+ "inner join employeemanagement.department_master dt on dt.id = e.dept_id "
			+  "inner join employeemanagement.job j on j.emp_id=e.id "
			+ "where (dm.id =:id1 or 0 =:id1) and (dt.id =:id2 or 0 =:id2) and (e.id =:id3 or 0=:id3)"
			+ "and dm.is_delete = false and dm.is_delete = false and e.com_id =:companyId and e.branch_id =:companyBranchId and e.is_delete=false and j.status_id=1 ", nativeQuery = true)
	Page<Employee> findAllIsDeleteFalseByDepartmentAndDesignationAndEmployee(@Param("id1") Long id1,
																			 @Param("id2") Long id2, @Param("id3") Long id3, @Param("companyId") Long companyId,
																			 @Param("companyBranchId") Long companyBranchId, Pageable pageable);

	@Query(value = "select e.* from employeemanagement.employee e "
			+ "inner join employeemanagement.designation_master dm on dm.id = e.designation_id "
			+  "inner join employeemanagement.job j on j.emp_id=e.id "
			+ "inner join employeemanagement.department_master dt on dt.id = e.dept_id "
			+ "where (dm.id =:id1 or 0 =:id1) and (dt.id =:id2 or 0 =:id2) and (e.id =:id3 or 0=:id3)"
			+ "and dm.is_delete = false and dm.is_delete = false and e.com_id =:companyId and e.branch_id =:companyBranchId and e.is_delete=false and j.status_id=1 ", nativeQuery = true)
	List<Employee> findAllIsDeleteFalseByDepartmentAndDesignationAndEmployee(@Param("id1") Long id1,
																			 @Param("id2") Long id2, @Param("id3") Long id3, @Param("companyId") Long companyId,
																			 @Param("companyBranchId") Long companyBranchId);
	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id=e.id where e.com_id =:companyId and e.branch_id =:companyBranchId  And   j.status_id !=2  and e.is_delete=false order by e.created_date desc", nativeQuery = true)
	List<Employee> findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchId(Long companyId,
																		Long companyBranchId);

	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id=e.id where e.com_id =:companyId and e.branch_id =:companyBranchId  And   j.status_id !=2  and e.is_delete=false order by e.created_date desc", nativeQuery = true)
	List<Employee> findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(Long companyId,
																							  Long companyBranchId);

	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id=e.id where e.com_id =:companyId and e.branch_id =:companyBranchId And e.id = :empId And   j.status_id !=2  and e.is_delete=false order by e.created_date desc", nativeQuery = true)
	List<Employee> findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDescNew(Long companyId,
																							  Long companyBranchId, Long empId);

	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id=e.id where e.com_id =:companyId and e.branch_id =:companyBranchId  And   j.status_id !=2  and e.is_delete=false ", nativeQuery = true)
	List<Employee> findByEmpIdJobActiveIsOrNot(Long companyId,
											   Long companyBranchId);

	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id=e.id where e.com_id =:companyId and e.branch_id =:companyBranchId  And   status_id !=2  and e.is_delete=false order by e.created_date desc", nativeQuery = true)
	List<Employee> findByEmpIdJobActiveIsOrNotOrderByCreateDate(Long companyId,
																Long companyBranchId);

	@Query(value = "Select COUNT(*) from employeemanagement.employee where pan_number=?1 ", nativeQuery = true)
	public Long checkPANExist(String panNo);

	@Query(value = "Select COUNT(*) from employeemanagement.employee where pan_number = ?1 and id != ?2", nativeQuery = true)
	public Long checkPANExistWithEmpId(String panNo, Long empId);

	@Query(value = "SELECT * FROM employeemanagement.employee e inner join employeemanagement.job j on j.emp_id=e.id WHERE e.is_delete = false AND e.com_id =:companyId AND e.branch_id =:branchId AND e.designation_id =:desi_Id  And   j.status_id =1  and e.is_delete=false order by e.created_date desc",nativeQuery = true)
	List<Employee> findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdAndDesignation(Long companyId,Long branchId,Long desi_Id);

	@Query(value = "select e.* from employeemanagement.employee e "
			+ "inner join employeemanagement.designation_master dm on dm.id = e.designation_id "
			+ " LEFT JOIN employeemanagement.job j on e.id = j.emp_id "
			+ "where (dm.id =:payDesgCd or 0 =:payDesgCd) and (e.id =:payEmpCd or 0=:payEmpCd)"
			+ "and dm.is_delete = false and e.com_id =:companyId and e.branch_id =:companyBranchId and j.status_id = 1", nativeQuery = true)
	List<Employee> findAllIsDeleteFalseByDesignationAndEmployee(@Param("payDesgCd") Long payDesgCd,
																@Param("payEmpCd") Long payEmpCd, @Param("companyId") Long companyId,
																@Param("companyBranchId") Long companyBranchId);

	@Query(value = "select e.* from employeemanagement.employee e "
			+ " inner join employeemanagement.job j on j.emp_id = e.id "
			+ " inner join employeemanagement.employeement_type et ON et.id = j.employement_type_id "
			+ " inner join employeemanagement.designation_master dm ON dm.id = e.designation_id "
			+ " where et.id =:empType and (dm.id =:desgId or 0 =:desgId) and e.com_id =:companyId and e.branch_id =:companyBranchId and e.is_delete = false and j.status_id = 1",nativeQuery = true)
	List<Employee> findAllIsDeleteFalseByEmployeMenttypeAndDesignation(@Param("empType") Long empType,
																	   @Param("desgId") Long desgId,@Param("companyId") Long companyId,@Param("companyBranchId") Long companyBranchId);



	@Query(value = "select * from employeemanagement.employee e \r\n"
			+ "inner join employeemanagement.job j on j.emp_id = e.id\r\n"
			+ "inner join employeemanagement.pay_emp_mst p on p.emp_id = e.id \r\n"
			+ "where e.com_id =:companyId and e.branch_id =:companyBranchId and j.status_id = 1 and e.is_delete = false",nativeQuery = true)
	List<Employee> findAllIsDeleteFalse(@Param("companyId") Long companyId,@Param("companyBranchId") Long companyBranchId);

	@Query(value = "select * from employeemanagement.employee e \r\n"
			+ "inner join employeemanagement.job j on j.emp_id = e.id\r\n"
			+ "where e.com_id =:companyId and e.branch_id =:companyBranchId and j.status_id = 1 and e.is_delete = false",nativeQuery = true)
	List<Employee> findAllIsDeleteFalseForManAdHoc(@Param("companyId") Long companyId,@Param("companyBranchId") Long companyBranchId);


//	@Query(value = "select e.* from employeemanagement.employee e "
//			+ " inner join employeemanagement.pay_emp_mst j on j.emp_id = e.id "
//			+ " inner join payroll.pay_emp_group_mst g ON g.id = j.group_code_id "
//			+ " where g.id=:empType and e.com_id =:companyId and e.branch_id =:companyBranchId ",nativeQuery = true)
//	List<Employee> findAllIsDeleteFalseBySalaryCategory(@Param("empType") Long empType,@Param("companyId") Long companyId,@Param("companyBranchId") Long companyBranchId);

	@Query(value = "select e.* from employeemanagement.employee e \r\n"
			+ "inner join employeemanagement.pay_emp_mst pem on pem.emp_id = e.id \r\n"
			+ "inner join employeemanagement.job j on j.emp_id = e.id\r\n"
			+ "inner join employeemanagement.employeement_type et on et.id = j.employement_type_id\r\n"
			+ "where et.id=:empType and e.com_id =:companyId and e.branch_id =:companyBranchId and e.is_delete = false and j.status_id = 1 ",nativeQuery = true)
	List<Employee> findAllIsDeleteFalseBySalaryCategory(@Param("empType") Long empType,@Param("companyId") Long companyId,@Param("companyBranchId") Long companyBranchId);

	@Query(value = "select e.* from employeemanagement.employee e \r\n"
			+ "inner join employeemanagement.job j on j.emp_id = e.id\r\n"
			+ "inner join employeemanagement.employeement_type et on et.id = j.employement_type_id\r\n"
			+ "where et.id=:empType and e.com_id =:companyId and e.branch_id =:companyBranchId and e.is_delete = false and j.status_id = 1 ",nativeQuery = true)
	List<Employee> findAllIsDeleteFalseBySalaryCategoryForManAdHoc(@Param("empType") Long empType,@Param("companyId") Long companyId,@Param("companyBranchId") Long companyBranchId);

	Employee findAllByempCodeAndCompanyIdAndCompanyBranchId(String empCode,Long companyId,Long companyBranchId);

	public Employee findByIdAndIsDeleteFalse(Long id);

	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id = e.id where e.com_id =:companyId and e.branch_id =:companyBranchId and e.is_delete =:b and j.status_id !=2", nativeQuery = true)
	List<Employee> findAllByCompanyAndCompanyBranchAndIsDelete(Long companyId, Long companyBranchId, boolean b);

	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id = e.id where e.com_id =:companyId and e.branch_id =:companyBranchId and j.status_id !=2 "
			+ "and e.is_delete =false and e.designation_id = :id", nativeQuery = true)
	List<Employee> findAllByDesignationAndIsDeleteFalseAndCompanyAndCompanyBranch(Long id, Long companyId,
																				  Long companyBranchId);

	@Query(value = "select e.* from employeemanagement.employee e "
			+ "inner join employeemanagement.designation_master dm on dm.id = e.designation_id "
			+ "where (dm.id =:payDesgCd or 0 =:payDesgCd) and (e.id =:payEmpCd or 0=:payEmpCd)"
			+ "and dm.is_delete = false and e.com_id =:companyId and e.branch_id =:companyBranchId ", nativeQuery = true)
	Page<Employee> findAllIsDeleteFalseByDesignationAndEmployee(@Param("payDesgCd") Long payDesgCd,
																@Param("payEmpCd") Long payEmpCd, @Param("companyId") Long companyId,
																@Param("companyBranchId") Long companyBranchId, Pageable pageable);

	@Query(value = "select emp.* from employeemanagement.employee emp "
			+ "inner join employeemanagement.designation_master dm on dm.id=emp.designation_id "
			+ "inner join public.company_branch_master cbm on cbm.id=emp.branch_id "
			+ "inner join public.company_master cm on cm.id = emp.com_id "
			+ "inner join employeemanagement.user_master um on um.emp_id=emp.id "
			+ "where emp.is_delete=false and um.system_role_type='DH' and emp.com_id=:companyId "
			+ "and emp.branch_id=:companyBranchId ", nativeQuery = true)
	List<Employee> findDHEmployeeByCompanyAndBranch(Long companyId, Long companyBranchId);

	@Query(value = "select emp.* from employeemanagement.employee emp "
			+ "inner join employeemanagement.designation_master dm on dm.id=emp.designation_id "
			+ "inner join public.company_branch_master cbm on cbm.id=emp.branch_id "
			+ "inner join public.company_master cm on cm.id = emp.com_id "
			+ "inner join employeemanagement.user_master um on um.emp_id=emp.id "
			+ "where emp.is_delete=false and um.system_role_type='HO' and emp.com_id=:companyId "
			+ "and emp.branch_id=:companyBranchId ", nativeQuery = true)
	List<Employee> findHOEmployeeByCompanyAndBranch(Long companyId, Long companyBranchId);

	@Query(value = "select emp.* from employeemanagement.employee emp "
			+ "inner join employeemanagement.designation_master dm on dm.id=emp.designation_id "
			+ "inner join public.company_branch_master cbm on cbm.id=emp.branch_id "
			+ "inner join public.company_master cm on cm.id = emp.com_id "
			+ "inner join employeemanagement.user_master um on um.emp_id=emp.id "
			+ "where emp.is_delete=false and um.system_role_type='HOD' and emp.com_id=:companyId "
			+ "and emp.branch_id=:companyBranchId ", nativeQuery = true)
	List<Employee> findHODEmployeeByCompanyAndBranch(Long companyId, Long companyBranchId);

	@Query(value = "select * from employeemanagement.employee where id=:empId ", nativeQuery = true)
	List<Employee> findByEmpId(Long empId);

	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id=e.id where e.id=:empId And status_id !=2  and e.is_delete=false", nativeQuery = true)
	List<Employee> findByEmpIdJobActive(Long empId);

	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id=e.id where  e.com_id=:companyId and e.branch_id=:branchId and  j.status_id !=2  and e.is_delete=false", nativeQuery = true)
	List<Employee> findByEmpIdJobActiveAndIsDeleteFalse(Long companyId, Long branchId);

	List<Employee> findAllByCompanyBranchIdAndIsDeleteFalse(Long companyBranchId);

	@Query(value = "select * from employeemanagement.employee where id in ( select distinct(erfd.emp_id) from employeemanagement.emp_reporting_officer_dtl erfd where erfd.dh_id =:empId )", nativeQuery = true)
	List<Employee> findAllEmpByDh(Long empId);

	@Query(value = "select * from employeemanagement.employee where id in ( select distinct(erfd.emp_id) from employeemanagement.emp_reporting_officer_dtl erfd where erfd.ho_id =:empId )", nativeQuery = true)
	List<Employee> findAllEmpByHo(Long empId);

	@Query(value = "select * from employeemanagement.employee where id in ( select distinct(erfd.emp_id) from employeemanagement.emp_reporting_officer_dtl erfd where erfd.hod_id =:empId )", nativeQuery = true)
	List<Employee> findAllEmpByHod(Long empId);

	@Modifying
	@Query(value = "update employeemanagement.employee set flag_for_approach_selection =:flag where (id=:empId or 0 =:empId)",nativeQuery = true)
	void enableDisableAllowanceConfiguration(@Param("flag") String flag, @Param("empId") Long empId);

	@Query(value = "select * from employeemanagement.employee where designation_id =:designation and com_id =:companyId and branch_id =:companyBranchId and is_delete = false", nativeQuery = true)
	List<Employee> findAllByDesignationAndCompanyIdAndCompanyBranchId(Long designation, Long companyId,
																	  Long companyBranchId);

	@Query(value = "select emp.* from employeemanagement.employee emp "
			+ "inner join employeemanagement.job j on j.emp_id = emp.id "
			+ "where j.employement_category_id = :category and emp.designation_id = :designation "
			+ "and j.com_id = :companyId and j.branch_id = :companyBranchId and emp.is_delete = false and j.status_id=1", nativeQuery = true)
	List<Employee> findAllByCategoryAndDesignationAndCompanyIdAndCompanyBranchId(Long category,
																				 Long designation, Long companyId, Long companyBranchId);

	@Query(value = "select emp.* from employeemanagement.employee emp "
			+ "inner join employeemanagement.job j on j.emp_id = emp.id "
			+ "where j.employement_category_id = :category "
			+ "and j.com_id = :companyId and j.branch_id = :companyBranchId", nativeQuery = true)
	List<Employee> findAllByCategoryAndCompanyIdAndCompanyBranchId(Long category, Long companyId,
																   Long companyBranchId);

	@Query(value = "select emp.* from employeemanagement.emplife_transfer et "
			+ "inner join employeemanagement.employee emp on emp.id = cast ( et.emp_id as int) "
			+ "inner join employeemanagement.job jb on jb.emp_id=emp.id "
			+ "inner join employeemanagement.employeement_type empt on empt.id = jb.employement_type_id "
			+ "where et.is_actual_relieved='1' and et.is_ta_claimed ='0' and emp.com_id=:companyId and emp.branch_id=:companyBranchId "
			+ "and (empt.code='permanent' or empt.code='temporary') "
			+ "and et.id not in(select application_ref_id from claimmanagement.claim_ta_advance_request "
			+ "where approval_status=true and application_ref_id is not null) and (:empId is null or emp.id=:empId) ", nativeQuery = true)
	List<Employee> getEmployeeListForTAOnTransfer(Long companyId, Long companyBranchId,Long empId);

	@Query(value = "select * from employeemanagement.employee emp "
			+ "left join employeemanagement.job jb on jb.emp_id=emp.id "
			+ "inner join employeemanagement.employeement_type empt on empt.id = jb.employement_type_id "
			+ "where emp.com_id=:companyId and emp.branch_id=:companyBranchId "
			+ "and (empt.code='permanent' or empt.code='temporary') and (:empId is null or emp.id=:empId)", nativeQuery = true)
	List<Employee> getEmployeeListForTAOnRetirement(Long companyId, Long companyBranchId, Long empId);

	@Query(value = "select emp.* from employeemanagement.employee emp "
			+ "inner join employeemanagement.job jb on jb.emp_id=emp.id "
			+ "inner join employeemanagement.employeement_type empt on empt.id = jb.employement_type_id "
			+ "inner join employeemanagement.emplife_death de on emp.id = cast ( de.emp_id as int) "
			+ "where emp.com_id=:companyId and emp.branch_id=:companyBranchId "
			+ "and (empt.code='permanent' or empt.code='temporary') and de.is_delete=false and "
			+ "(:empId is null or emp.id=:empId) ", nativeQuery = true)
	List<Employee> getEmployeeListForTAOnFamilyOdDeceased(Long companyId, Long companyBranchId,Long empId);

	// Added By Vidhi Patel

	@Query(value = "select Emp.* from employeemanagement.employee AS Emp "
			+ "left join employeemanagement.job g on Emp.id=g.emp_id "
			+ "where Emp.com_id = :comId and Emp.branch_id = :branchId and Emp.is_delete = false "
			+ "and (g.employement_type_id =1 or g.employement_type_id=2)", nativeQuery = true)
	List<Employee> findAllByTaTypeId1Or6(@Param("comId") Long comId, @Param("branchId") Long branchId);

	@Query(value = "select Emp.* from employeemanagement.employee as Emp left join employeemanagement.job g on Emp.id=g.emp_id "
			+ "where Emp.id not in (select c.emp_id from claimmanagement.claim c inner join "
			+ "claimmanagement.claim_ta_reimbursement as ct on c.id=ct.claim_id where ct.ta_type_id=4 and c.is_delete=false ) "
			+ "and AGE(g.retirement_date, CURRENT_DATE) between INTERVAL '0 days' AND INTERVAL '59 days' "
			+ "and (g.employement_type_id =1 or g.employement_type_id=2) and Emp.com_id = :comId and "
			+ "Emp.branch_id = :branchId and Emp.is_delete = false", nativeQuery = true)
	List<Employee> findAllByTaType4(@Param("comId") Long comId, @Param("branchId") Long branchId);

//	@Query(value = "Select Emp.* from employeemanagement.emplife_transfer A inner join employeemanagement.employee Emp on "
//			+ "CAST(Emp.id as text) = A.emp_cd left join employeemanagement.job g on Emp.id=g.emp_id where "
//			+ "A.is_actual_relieved = '1' and A.is_el_credited = '1' and A.is_ta_claimed = '0' and "
//			+ "Emp.com_id = :comId and Emp.branch_id = :branchId and Emp.is_delete = false "
//			+ "and A.id not in(select application_ref_id from claimmanagement.claim_ta_advance_request where approval_status = true) "
//			+ "and A.id not in(select application_ref_id from claimmanagement.claim_ta_advance_request where (approval_status IS NULL) or "
//			+ "(approval_status=false)) and (g.employement_type_id =1 or g.employement_type_id=2)", nativeQuery = true)
//	List<Employee> findAllByTaType2Or7(@Param("comId") Long comId, @Param("branchId") Long branchId);

	@Query(value = "SELECT Distinct  Emp.Emp_id,Emp.Emp_Nm,Emp.Desg_Nm, Emp.Branch_Nm, Emp.com_id,Emp.Branch_id, A.id\r\n"
			+ "FROM employeemanagement.emplife_transfer A \r\n"
			+ "inner join employeemanagement.vw_empdeptdesgnew Emp on Emp.emp_id =A.emp_id \r\n"
			+ "left join employeemanagement.job g on Emp.Emp_id=g.emp_id Where A.Is_Actual_Relieved='1' \r\n"
			+ "and Is_TA_Claimed ='0' and Emp.com_id = :comId and Emp.Branch_id = :branchId \r\n"
			+ "and A.id not in(select application_ref_id from claimmanagement.claim_ta_advance_request \r\n"
			+ "	where (Approval_Status=false) and application_ref_id IS NOT NULL) \r\n"
			+ "and (G.employement_type_id=1 or G.employement_type_id=2)  ", nativeQuery = true)
	List<Object> findAllByTaType2Or7(@Param("comId") Long comId, @Param("branchId") Long branchId);

	@Query(value = "Select Emp.* From employeemanagement.employee Emp inner join employeemanagement.employee A on Emp.id =A.id "
			+ "left join employeemanagement.job g on Emp.id=g.emp_id where A.emp_service_status='Death' "
			+ "and (g.employement_type_id =1 or g.employement_type_id=2) and Emp.com_id = :comId "
			+ "and Emp.branch_id = :branchId and Emp.is_delete = false", nativeQuery = true)
	List<Employee> findAllByTaType5(@Param("comId") Long comId, @Param("branchId") Long branchId);

	@Query(value = "SELECT e.* \r\n"
			+ "FROM employeemanagement.emp_personal_info epi \r\n"
			+ "inner join employeemanagement.employee e on epi.emp_id = e.id\r\n"
			+ "WHERE \r\n"
			+ "e.com_id =:companyId and e.branch_id=:companyBranchId and\r\n"
			+ "EXTRACT(month FROM epi.date_of_birth) = EXTRACT(month FROM CURRENT_DATE)\r\n"
			+ "AND EXTRACT(day FROM epi.date_of_birth) = EXTRACT(day FROM CURRENT_DATE)",nativeQuery = true)
	List<Employee> findAllEmployeeBirthDayToday(@Param("companyId") Long companyId,@Param("companyBranchId") Long companyBranchId);

	// Link :- https://stackoverflow.com/questions/43767284/postgres-interval-not-working-with-native-spring-data-jpa-query
	@Query(value = "SELECT e.* \r\n"
			+ "FROM employeemanagement.employee e\r\n"
			+ "INNER JOIN employeemanagement.emplife_retirement er ON CAST(e.id AS VARCHAR) = er.emp_id\r\n"
			+ "WHERE \r\n"
			+ "e.com_id =:companyId AND e.branch_id=:companyBranchId \r\n"
			+ "AND e.emp_service_status = 'Retired'\r\n"
			+ "AND er.retirement_order_date BETWEEN NOW() AND NOW() + (interval '1' day) * :days ;",nativeQuery = true)
	List<Employee> findAllEmployeeWhichIsRetireInNDays(@Param("companyId") Long companyId,
													   @Param("companyBranchId") Long companyBranchId
			,@Param("days") Long days);

	List<Employee> findByEmpServiceStatusInAndCompanyIdAndCompanyBranchId(List<String> serviceStatuses, Long companyId, Long companyBranchId);

	// tender resignation date means end day of notice period
	// so send notification still notice period is not end
	@Query(value = "SELECT e.* \r\n"
			+ "FROM employeemanagement.employee e\r\n"
			+ "INNER JOIN employeeservicecycle.emplife_resignation_master er ON e.id  = er.employee_cd\r\n"
			+ "WHERE \r\n"
			+ "e.com_id =:companyId AND e.branch_id=:companyBranchId \r\n"
			+ "AND e.emp_service_status = 'Resigned'\r\n"
			+ "AND er.resignation_date BETWEEN er.resignation_date AND er.resignation_tender_date",nativeQuery = true)
	List<Employee> findAllEmployeeWhichIsResigned(@Param("companyId") Long companyId,
												  @Param("companyBranchId") Long companyBranchId);

	public Employee findByIdAndIsDeleteFalseAndCompanyIdAndCompanyBranchId(Long id,Long companyId,Long companyBranchId);

	@Query(value = "select * from employeemanagement.employee e "
			+ " inner join payroll.pay_adjust_trn_trg pay on pay.employee_id = e.id "
			+ " inner join payroll.element_of_pay_system_master element on element.id = pay.element_id "
			+ " where pay.employee_id !=:parseLong and pay.adjustment_month !=:month and pay.adjustment_year !=:year"
			+ " and pay.element_id !=:elementPay ", nativeQuery = true)
	Optional<Employee> findByIdAndMonthAndYearAndElementName(long parseLong, String month,
															 String year, Long elementPay);

	@Query(value = "select emp.* from employeemanagement.employee emp  join employeemanagement.job j on emp.id= j.emp_id "
			+ "where emp.id not in (select employee_id from payroll.pay_adjust_trn_trg payAdjust where (:emp is null or employee_id =:emp)  "
			+ "				 and adjustment_month not in(:month) "
			+ "				 and adjustment_year not in(:year) "
			+ "				 and element_id not in(:element)) and (:designation is null or emp.designation_id =:designation) and emp.com_id =:companyId  "
			+ "				 and  j.status_id=1 "
			+ "				 and emp.branch_id =:companyBranchId and emp.is_delete = false and (:emp is null or emp.id =:emp)", nativeQuery = true)
	List<Employee> findAllByMonthAndYearAndIsDeleteFalseAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
			Long companyId, Long companyBranchId, String month, String year, Long element, Long designation, Long emp);


	@Query(value = "select emp.* from employeemanagement.employee emp  join employeemanagement.job j on emp.id= j.emp_id "
			+ "where emp.id not in (select employee_id from payroll.pay_adjust_trn_trg payAdjust where (:emp is null or employee_id =:emp)  "
			+ "				 and adjustment_month not in(:month) "
			+ "				 and adjustment_year not in(:year) )"
			+ "				 and (:designation is null or emp.designation_id =:designation) and emp.com_id =:companyId  "
			+ "				 and  j.status_id=1 "
			+ "				 and emp.branch_id =:companyBranchId and emp.is_delete = false and (:emp is null or emp.id =:emp)", nativeQuery = true)
	List<Employee> findAllByMonthAndYearAndIsDeleteFalseAndDesignationIdAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDesc(
			Long companyId, Long companyBranchId, String month, String year, Long designation, Long emp);

//	@Query(value = "SELECT emp.* FROM employeemanagement.employee emp "
//			+ "JOIN employeemanagement.job j ON emp.id = j.emp_id "
//			+ "WHERE emp.id NOT IN ("
//			+ "    SELECT employee_id FROM payroll.pay_adjust_trn_trg payAdjust "
//			+ "    WHERE (:empIds IS NULL OR employee_id = ANY (:empIds)) "
//			+ "    AND adjustment_month NOT IN (:month) "
//			+ "    AND adjustment_year NOT IN (:year) "
//			+ ") "
//			+ "AND (:designation IS NULL OR emp.designation_id = :designation) "
//			+ "AND emp.com_id = :companyId "
//			+ "AND j.status_id = 1 "
//			+ "AND emp.branch_id = :companyBranchId "
//			+ "AND emp.is_delete = FALSE "
//			+ "AND (:empIds IS NULL OR emp.id = ANY (:empIds))",
//			nativeQuery = true)
//	List<Employee> findAllByMonthAndYearAndIsDeleteFalseAndDesignationIdAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDescNew(
//			Long companyId, Long companyBranchId, String month, String year, Long designation, List<Long> empIds);


//	@Query(value = "SELECT emp.* FROM employeemanagement.employee emp "
//			+ "JOIN employeemanagement.job j ON emp.id = j.emp_id "
//			+ "WHERE emp.id NOT IN ("
//			+ "    SELECT employee_id FROM payroll.pay_adjust_trn_trg payAdjust "
//			+ "    WHERE (:empIds IS NULL OR employee_id = ANY (ARRAY[:empIds]::bigint[])) "
//			+ "    AND adjustment_month NOT IN (:month) "
//			+ "    AND adjustment_year NOT IN (:year) "
//			+ ") "
//			+ "AND (:designation IS NULL OR emp.designation_id = :designation) "
//			+ "AND emp.com_id = :companyId "
//			+ "AND j.status_id = 1 "
//			+ "AND emp.branch_id = :companyBranchId "
//			+ "AND emp.is_delete = FALSE "
//			+ "AND (:empIds IS NULL OR emp.id = ANY (ARRAY[:empIds]::bigint[]))",
//			nativeQuery = true)
//	List<Employee> findAllByMonthAndYearAndIsDeleteFalseAndDesignationIdAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDescNew(
//			Long companyId, Long companyBranchId, String month, String year, Long designation, List<Long> empIds);

	@Query(value = "SELECT emp.* FROM employeemanagement.employee emp "
			+ "JOIN employeemanagement.job j ON emp.id = j.emp_id "
			+ "WHERE emp.id NOT IN ("
			+ "    SELECT employee_id FROM payroll.pay_adjust_trn_trg payAdjust "
			+ "    WHERE ((:empIds) IS NULL OR employee_id IN (:empIds)) "
			+ "    AND adjustment_month NOT IN (:month) "
			+ "    AND adjustment_year NOT IN (:year) "
			+ ") "
			+ "AND (:designation IS NULL OR emp.designation_id = :designation) "
			+ "AND emp.com_id = :companyId "
			+ "AND j.status_id = 1 "
			+ "AND emp.branch_id = :companyBranchId "
			+ "AND emp.is_delete = FALSE "
			+ "AND ((:empIds) IS NULL OR emp.id IN (:empIds))",
			nativeQuery = true)
	List<Employee> findAllByMonthAndYearAndIsDeleteFalseAndDesignationIdAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDescNew(
			Long companyId, Long companyBranchId, String month, String year, Long designation, List<Long> empIds);

//	@Query(value = "SELECT emp.* FROM employeemanagement.employee emp "
//			+ "JOIN employeemanagement.job j ON emp.id = j.emp_id "
//			+ "WHERE emp.id NOT IN ("
//			+ "    SELECT employee_id FROM payroll.pay_adjust_trn_trg payAdjust "
//			+ "    WHERE (:empIds IS NULL OR employee_id = ANY (SELECT unnest(:empIds))) "
//			+ "    AND adjustment_month NOT IN (:month) "
//			+ "    AND adjustment_year NOT IN (:year) "
//			+ ") "
//			+ "AND (:designation IS NULL OR emp.designation_id = :designation) "
//			+ "AND emp.com_id = :companyId "
//			+ "AND j.status_id = 1 "
//			+ "AND emp.branch_id = :companyBranchId "
//			+ "AND emp.is_delete = FALSE "
//			+ "AND (:empIds IS NULL OR emp.id = ANY (SELECT unnest(:empIds)))",
//			nativeQuery = true)
//	List<Employee> findAllByMonthAndYearAndIsDeleteFalseAndDesignationIdAndCompanyIdAndCompanyBranchIdOrderByCreatedDateDescNew(
//			Long companyId, Long companyBranchId, String month, String year, Long designation, List<Long> empIds);






	@Query(value = "select emp.*, pay.monthly_gross_salary from employeemanagement.employee emp "
			+ "join employeemanagement.pay_emp_mst pay on pay.emp_id = emp.id "
			+ "where (:emp is null or pay.emp_id =:emp) "
			+ "and (:designation is null or emp.designation_id =:designation) and emp.com_id =:companyId "
			+ "and emp.branch_id =:companyBranchId and emp.is_delete = false", nativeQuery = true)
	List<Employee> findByIdAndSalary(Long companyId, Long companyBranchId,Long emp, Long designation);

	@Query(value = "select e.* from employeemanagement.employee e\r\n"
			+ "inner join employeemanagement.designation_master dm on dm.id = e.designation_id\r\n"
			+ "inner join employeemanagement.department_master dt on dt.id = e.dept_id\r\n"
			+ "where ((dm.id =:id1 or 0 =:id1) and (dt.id =:id2 or 0 =:id2) and (e.id =:id3 or 0=:id3)"
			+ "AND dm.is_delete = false and dt.is_delete = false and e.com_id =:companyId and e.branch_id =:companyBranchId\r\n)"
			+ "AND (CAST(e.id as text) NOT IN (SELECT unnest(string_to_array(opcm.employee_id, ',')) FROM \r\n"
			+ "				leaveattandance.overtime_policy_configuration_master opcm WHERE \r\n"
			+ "							  opcm.is_delete = false))", nativeQuery = true)
	Page<Employee> findAllOverTimeEmployeeList(@Param("id1") Long id1,
											   @Param("id2") Long id2, @Param("id3") Long id3, @Param("companyId") Long companyId,
											   @Param("companyBranchId") Long companyBranchId, Pageable pageable);

	@Query(value = "select e.* from employeemanagement.employee e\r\n"
			+ "inner join employeemanagement.designation_master dm on dm.id = e.designation_id\r\n"
			+ "inner join employeemanagement.department_master dt on dt.id = e.dept_id\r\n"
			+ "where ((dm.id =:id1 or 0 =:id1) and (dt.id =:id2 or 0 =:id2) and (e.id =:id3 or 0=:id3)"
			+ "AND dm.is_delete = false and dt.is_delete = false and e.com_id =:companyId and e.branch_id =:companyBranchId\r\n)"
			+ "AND (CAST(e.id as text) NOT IN (SELECT unnest(string_to_array(opcm.employee_id, ',')) FROM \r\n"
			+ "				leaveattandance.overtime_policy_configuration_master opcm WHERE \r\n"
			+ "							  opcm.is_delete = false AND opcm.id != :id))", nativeQuery = true)
	Page<Employee> findAllOverTimeEmployeeList(@Param("id1") Long id1,
											   @Param("id2") Long id2, @Param("id3") Long id3, @Param("companyId") Long companyId,
											   @Param("companyBranchId") Long companyBranchId, @Param("id") Long id, Pageable pageable);

	//	@Query(value = "SELECT \r\n"
//			+ "    emp.*\r\n"
//			+ "FROM employeemanagement.employee  emp\r\n"
//			+ "LEFT JOIN (\r\n"
//			+ "    SELECT er.employee_cd \r\n"
//			+ "    FROM employeeservicecycle.emplife_resignation_master er \r\n"
//			+ "    WHERE (er.resignation_date BETWEEN :fromDate AND :toDate)\r\n"
//			+ "    GROUP BY er.employee_cd\r\n"
//			+ ") res ON emp.id = res.employee_cd\r\n"
//			+ "LEFT JOIN (\r\n"
//			+ "    SELECT err.emp_cd \r\n"
//			+ "    FROM employeemanagement.emplife_retirement err \r\n"
//			+ "    WHERE (err.sys_retirement_date BETWEEN :fromDate AND :toDate)\r\n"
//			+ "    GROUP BY err.emp_cd 	\r\n"
//			+ ") ret ON emp.id = ret.emp_cd\r\n"
//			+ "LEFT JOIN (\r\n"
//			+ "    SELECT ed.emp_cd \r\n"
//			+ "    FROM employeemanagement.emplife_death ed \r\n"
//			+ "    WHERE (ed.death_date BETWEEN :fromDate AND :toDate)\r\n"
//			+ "    GROUP BY ed.emp_cd	\r\n"
//			+ ") death ON emp.id = death.emp_cd\r\n"
//			+ "WHERE emp.id IN (res.employee_cd, ret.emp_cd, death.emp_cd) \r\n"
//			+ "AND emp.com_id = :companyId \r\n"
//			+ "AND emp.branch_id = :companyBranchId\r\n"
//			+ "AND emp.id NOT IN (SELECT lec.employee_id FROM payroll.leave_encashment lec)\r\n"
//			+ "AND (\r\n"
//			+ "    emp.id IN (res.employee_cd) AND emp.emp_service_status = 'Resigned'\r\n"
//			+ "    OR emp.id IN (ret.emp_cd) AND emp.emp_service_status = 'Retired'\r\n"
//			+ "    OR emp.id IN (death.emp_cd) AND emp.emp_service_status = 'Death'\r\n"
//			+ ")\r\n"
//			+ "",nativeQuery = true)
	@Query(value = "SELECT emp.* \r\n"
			+ "FROM employeemanagement.employee  emp \r\n"
			+ "where emp.com_id = :companyId \r\n"
			+ "AND emp.branch_id = :companyBranchId \r\n"
			+ "and emp.is_delete = false", nativeQuery = true)
	List<Employee> findAllByEmpServiceStatus(@Param("companyId")Long companyId, @Param("companyBranchId")Long companyBranchId);

	@Query(value = "select dept.dept_name, des.designation_name, fam.id, fam.first_name, fam.middle_name, fam.last_name from employeemanagement.employee emp\r\n"
			+ "inner join employeemanagement.department_master dept on dept.id = emp.dept_id\r\n"
			+ "inner join employeemanagement.designation_master des on des.id = emp.designation_id\r\n"
			+ "left join employeemanagement.emp_family_dtl fam ON fam.emp_id = emp.id\r\n"
			+ "where emp.id =:empId", nativeQuery = true)
	List<Object[]> getEmployeeDetails(Long empId);

	@Query(value = "select * from employeemanagement.employee where id=:id ", nativeQuery = true)
	Employee findByMstId(Long id);

	@Query(value = "select * from employeemanagement.employee where emp_code_postfix = :emp_code_postfix and com_id=:companyId and branch_id=:branchId", nativeQuery = true)
	List<Employee> findbycodepostfix(@Param("emp_code_postfix")String emp_code_postfix ,@Param("companyId") Long companyId,@Param("branchId") Long branchId);


	@Query(value = " select emp.* from employeemanagement.employee emp  "
			+ "	inner join employeemanagement.job j on j.emp_id = emp.id  "
			+ "	where (j.employement_type_id =:id1 or 0 =:id1)"
			//+ " and (emp.designation_id =:id2  or 0 =:id2) "
			+ " and (j.emp_id =:id3  or 0 =:id3)"
			+ "	and j.com_id =:companyId and j.branch_id =:companyBranchId and emp.is_delete = false and j.status_id = 1", nativeQuery = true)
	List<Employee> findAllIsDeleteFalseByEmployeeCategoryAndDesignationAndEmployee(@Param("id1") Long id1,
																				   //@Param("id2") Long id2,
																				   @Param("id3") Long id3,@Param("companyId") Long companyId,
																				   @Param("companyBranchId") Long companyBranchId);


	@Query(value = " select emp.* from employeemanagement.employee emp  "
			+ "	inner join employeemanagement.job j on j.emp_id = emp.id  "
			+ "	where (j.employement_type_id =:id1 or 0 =:id1) and (emp.designation_id =:id2  or 0 =:id2) "
			+ "	and j.com_id =:companyId and j.branch_id =:companyBranchId and emp.is_delete = false ", nativeQuery = true)
	List<Employee> findAllIsDeleteFalseByEmployeeCategoryAndDesignation(@Param("id1") Long id1,
																		@Param("id2") Long id2,@Param("companyId") Long companyId,
																		@Param("companyBranchId") Long companyBranchId);


	@Query(value = " select emp.* from employeemanagement.employee emp  "
			+ "	inner join employeemanagement.job j on j.emp_id = emp.id  "
			+ "	where (j.employement_type_id =:id1 or 0 =:id1) "
			+ "	and j.com_id =:companyId and j.branch_id =:companyBranchId and emp.is_delete = false and j.status_id = 1 ", nativeQuery = true)
	List<Employee> findAllIsDeleteFalseByEmployeeCategory(@Param("id1") Long id1,@Param("companyId") Long companyId,
														  @Param("companyBranchId") Long companyBranchId);

	@Query(value = " select emp.* from employeemanagement.employee emp  "
			+ "	inner join employeemanagement.job j on j.emp_id = emp.id  "
			//+ "	where (j.employement_type_id =:id1 or 0 =:id1)"
			//+ " and (emp.designation_id =:id2  or 0 =:id2) "
			+ " where (:id1 is null or emp.id=:id1)"
			+ "	and emp.com_id =:companyId and emp.branch_id =:companyBranchId and emp.is_delete = false and j.status_id = 1 ", nativeQuery = true)
	List<Employee> findAllIsDeleteFalseByEmployee(@Param("id1") Long id1,
												  //@Param("id2") Long id2,
												  @Param("companyId") Long companyId,
												  @Param("companyBranchId") Long companyBranchId);

	@Query(value = "select emp.* from employeemanagement.employee emp inner Join employeemanagement.job j on j.emp_id = emp.id  where "
			+ "emp.com_id = :companyId and emp.branch_id = :companyBranchId and (emp.id = :empId or :empId = 0) and emp.is_delete=false  and j.status_id=1", nativeQuery = true)
	List<Employee> findAllEmployee(@Param("companyId") Long companyId,
								   @Param("companyBranchId") Long companyBranchId,@Param("empId") Long empId);

	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id=e.id where e.com_id =:companyId and e.branch_id =:companyBranchId  And   status_id !=2  and e.is_delete=false order by e.created_date desc", nativeQuery = true)
	Page<Employee> findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchId(Long companyId, Long companyBranchId,
																		Pageable pageable);
	@Query(value = "select * from employeemanagement.employee e inner join employeemanagement.job j on j.emp_id=e.id where e.com_id =:companyId and e.branch_id =:companyBranchId And e.id =:id  And   status_id !=2  and e.is_delete=false order by e.created_date desc", nativeQuery = true)
	Page<Employee> findAllByIsDeleteFalseAndCompanyIdAndCompanyBranchIdAndId(Long companyId, Long companyBranchId,Long id,
																			 Pageable pageable);

	@Query(value = "select gender from employeemanagement.emp_personal_info where emp_id = :emp_id",nativeQuery = true)
	String getGenderByEmployeeId(Long emp_id);

	@Query(value = "SELECT \r\n"
			+ "    e.id, \r\n"
			+ "    e.emp_code, \r\n"
			+ "    CONCAT(e.salutation, ' ',e.first_name, ' ', e.last_name) as emp_name, \r\n"
			+ "    epi.date_of_birth, \r\n"
			+ "    epi.date_of_marriage, "
			+ "	e.date_of_appointment,\r\n"
			+ "    ecd.corporate_email,"
			+ " cast(EXTRACT(YEAR FROM AGE(CURRENT_DATE, epi.date_of_birth)) as character varying) AS years_completed_birthday,\r\n"
			+ "    cast(EXTRACT(MONTH FROM AGE(CURRENT_DATE, epi.date_of_birth)) as character varying) AS months_completed_birthday,\r\n"
			+ "\r\n"
			+ "    -- Calculate months and years completed since marriage (Anniversary)\r\n"
			+ "    cast(EXTRACT(YEAR FROM AGE(CURRENT_DATE, e.date_of_appointment))as character varying) AS years_completed_anniversary,"
			+ "cast(EXTRACT(MONTH FROM AGE(CURRENT_DATE, e.date_of_appointment))as character varying) AS months_completed_anniversary\r\n"
			+ "\r\n"
			+ "FROM employeemanagement.emp_personal_info epi\r\n"
			+ "LEFT JOIN employeemanagement.employee e ON epi.emp_id = e.id "
			+ "inner join employeemanagement.job j on j.emp_id = e.id \r\n"
			+ "LEFT JOIN (\r\n"
			+ "    SELECT DISTINCT ON (ecd.emp_id) \r\n"
			+ "        ecd.emp_id, \r\n"
			+ "        ecd.corporate_email\r\n"
			+ "    FROM employeemanagement.emp_contact_dtl ecd\r\n"
			+ "    ORDER BY ecd.emp_id \r\n"
			+ ") ecd ON ecd.emp_id = e.id\r\n"
			+ "WHERE e.com_id = :com_id and e.branch_id = :branch_id "
			+ "  and j.status_id = 1 \r\n"
			+ "  AND e.is_delete = false;", nativeQuery = true)
	List<Object[]>getEmployeesWithBirthdayOrAnniversary(Long com_id, Long branch_id);
	

	@Query(value = "SELECT e.* " +
			"FROM employeemanagement.employee e " +
			"INNER JOIN employeemanagement.designation_master dm ON dm.id = e.designation_id " +
			"INNER JOIN employeemanagement.department_master dt ON dt.id = e.dept_id " +
			"INNER JOIN employeemanagement.job j ON j.emp_id = e.id " +
			"WHERE (dm.id = :id1 OR 0 = :id1) " +
			"  AND (dt.id = :id2 OR 0 = :id2) " +
			"  AND (e.id = :id3 OR 0 = :id3) " +
			"  AND dm.is_delete = FALSE " +
			"  AND dt.is_delete = FALSE " +
			"  AND e.com_id = :companyId " +
			"  AND e.branch_id = :companyBranchId " +
			"  AND e.is_delete = FALSE " +
			"  AND j.status_id = 1 ",
			nativeQuery = true)
	Page<Employee> findAllIsDeleteFalseByDepartmentAndDesignationAndEmployeeOrderByIds(
			@Param("id1") Long id1,
			@Param("id2") Long id2,
			@Param("id3") Long id3,
			@Param("companyId") Long companyId,
			@Param("companyBranchId") Long companyBranchId,
			Pageable pageable);

//@Query(value = "select e.* from employeemanagement.employee e \n" +
//		"inner join employeemanagement.job j on j.emp_id=e.id \n" +
//		"where e.com_id =:companyId and e.branch_id =:companyBranchId \n" +
//		"And   j.status_id <>2  and e.is_delete=false \n" +
//		"Order by \n" +
//		"\tCASE\n" +
//		"\tWHEN e.id in (SELECT emp_id FROM workflow.wf_rule_configuration WHERE com_id = :companyId AND is_delete = false and id = :id) THEN 0 ELSE 1 " +
//		"\tEND ASC", nativeQuery = true)
//	Page<Employee> findByEmployeeAppWorkflowContainingEmployees(Long companyId, Long companyBranchId, Long id, Pageable pageable);

	@Query(value = " SELECT e.* " +
			"FROM employeemanagement.employee e " +
			"INNER JOIN employeemanagement.job j ON j.emp_id = e.id " +
			"LEFT JOIN workflow.wf_rule_configuration w " +
			"    ON cast(e.id as text) = ANY(w.employee_ids) " +
			"    AND w.com_id = :companyId " +
			"    AND w.is_delete = false " +
			"    AND w.id = :id " +
			"WHERE e.com_id = :companyId " +
			"  AND e.branch_id = :companyBranchId " +
			"  AND j.status_id <> 2 " +
			"  AND e.is_delete = false ", nativeQuery = true)
	Page<Employee> findByEmployeeAppWorkflowContainingEmployees(Long companyId, Long companyBranchId, Long id, Pageable pageable);


	@Query(value = " SELECT e.* " +
			"FROM employeemanagement.employee e " +
			"INNER JOIN employeemanagement.job j ON j.emp_id = e.id " +
			"LEFT JOIN workflow.wf_rule_configuration w " +
			"    ON cast(e.id as text) = ANY(w.employee_ids) " +
			"    AND w.com_id = :companyId " +
			"    AND w.is_delete = false " +
			"    AND w.id = :id " +
			"WHERE e.com_id = :companyId " +
			"  AND e.branch_id = :companyBranchId " +
			"  AND j.status_id <> 2 " +
			"  AND e.is_delete = false " +
			" ORDER BY   " +
			"    (w.id IS NOT NULL) DESC, " +
			"    e.id limit :limit offset :offset", nativeQuery = true)
	List<Employee> findByEmployeeAppWorkflowContainingEmployeesWithLimitandOffset(Long companyId, Long companyBranchId, Long id, int limit, int offset);

	@Query(value = "SELECT e.* " +
			"FROM employeemanagement.employee e " +
			"INNER JOIN employeemanagement.designation_master dm ON dm.id = e.designation_id " +
			"INNER JOIN employeemanagement.department_master dt ON dt.id = e.dept_id " +
			"INNER JOIN employeemanagement.job j ON j.emp_id = e.id " +
			"WHERE (dm.id = :id1 OR 0 = :id1) " +
			"  AND (dt.id = :id2 OR 0 = :id2) " +
			"  AND (e.id = :id3 OR 0 = :id3) " +
			"  AND dm.is_delete = FALSE " +
			"  AND dt.is_delete = FALSE " +
			"  AND e.com_id = :companyId " +
			"  AND e.branch_id = :companyBranchId " +
			"  AND e.is_delete = FALSE " +
			"  AND j.status_id = 1 " +
			" ORDER BY (CASE WHEN e.id IN (:ids) THEN 0 ELSE 1 END), e.emp_code ASC "+
			" LIMIT :limit OFFSET :offset ",nativeQuery = true)
	List<Employee> findAllIsDeleteFalseByDepartmentAndDesignationAndEmployeeOrderByIds1(
			@Param("id1") Long id1,
			@Param("id2") Long id2,
			@Param("id3") Long id3,
			@Param("companyId") Long companyId,
			@Param("companyBranchId") Long companyBranchId,
			@Param("ids") List<BigInteger> ids,
			@Param("limit") int limit,
			@Param("offset") int offset);



}
