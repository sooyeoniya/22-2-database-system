
public enum Nodes02 {
	Tables, 
	Scalar_Functions, 
	Table_Functions, 
	Stored_Procedures, 
	Triggers,
	
	// Table_Functions
	getColunmList,
	getFunctionList,
	getParameters,
	getPKFK,
	getProcedure,
	getRCtable,
	getTableList,
	getTriggers1,
	getTriggers2,
	getUDtrigger,
	
	// Scalar_Functions
	CalculateArea,
	GetAge,
	getSum,
	rectangleArea,
	reverseCustName,
	
	// Stored_Procedures
	addDiscountCoupon,
	addInstructor,
	addLecture,
	addLectureRoom,
	addPossess,
	addStudent,
	addTake,
	deleteDiscountCoupon,
	deleteInstructor,
	deleteLecture,
	deleteLectureRoom,
	deletePossess,
	deleteStudent,
	deleteTake,
	updateDiscountCoupon,
	updateInstructor,
	updateLecture,
	updateLectureRoom,
	updatePossess,
	updateStudent,
	updateTake,
	
	// Triggers
	myTrigger_D,
	tr_deleteDiscountCoupon,
	tr_updateDiscountCoupon,
	
	myTrigger_I,
	tr_deleteInstructor,
	tr_updateInstructor,
	
	myTrigger_L,
	tr_deleteLecture,
	tr_updateLecture,
	
	myTrigger_LR,
	tr_deleteLectureRoom,
	tr_updateLectureRoom,
	
	myTrigger_P,
	tr_deletePossess,
	tr_updatePossess,
	
	myTrigger_S,
	tr_deleteStudent,
	tr_updateStudent,
	
	myTrigger_T,
	tr_deleteTake,
	tr_updateTake
	
}