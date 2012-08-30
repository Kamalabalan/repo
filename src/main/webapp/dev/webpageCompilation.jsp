<%@ page import="teammates.common.Common"%>
<!DOCTYPE html>
<html>
	<head>
		<link rel="shortcut icon" href="/favicon.png"></link>
		<meta http-equiv="X-UA-Compatible" content="IE=8"></link>
		<title>Teammates - Webpage Compilation</title>
		
		<link rel=stylesheet href="webpageCompilation.css" type="text/css"></link>
		<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
		<script language="JavaScript" src="/js/tooltip.js"></script>
		
	</head>
	<body id="compilation">
		<div id="dhtmltooltip"></div>
		<div class="info">Coordinator Home Page</div>
		<div id="coordHomePage" class="wrapper"></div>
		
		<div class="info">Coordinator Add Course Page</div>
		<div id="coordAddCoursePage" class="wrapper"></div>
		
		<div class="info">Coordinator Enroll Students Page</div>
		<div id="coordEnrollPage" class="wrapper"></div>
		
		<div class="info">Coordinator Course Details Page</div>
		<div id="coordCourseDetailsPage" class="wrapper"></div>
		
		<div class="info">Coordinator Student Details Page</div>
		<div id="coordCourseStudentDetailsPage" class="wrapper"></div>
		
		<div class="info">Coordinator Student Edit Page</div>
		<div id="coordCourseStudentEditPage" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Page</div>
		<div id="coordCourseEvalPage" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Edit Page</div>
		<div id="coordCourseEvalEditPage" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Results Page (coordinatorEvaluationSummaryTable)</div>
		<div id="coordCourseEvalResultsPage1" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Results Page (coordinatorEvaluationDetailedReviewerTable)</div>
		<div id="coordCourseEvalResultsPage2" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Results Page (coordinatorEvaluationDetailedRevieweeTable)</div>
		<div id="coordCourseEvalResultsPage3" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Submission View Page</div>
		<div id="coordCourseEvalSubmissionViewPage" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Submission Edit Page</div>
		<div id="coordCourseEvalSubmissionEditPage" class="wrapper"></div>
		<br></br>
		<br></br>
		<br></br>
		<br></br>
		<div class="info">Student Home Page</div>
		<div id="studentHomePage" class="wrapper"></div>
		
		<div class="info">Student Course Details Page</div>
		<div id="studentCourseDetailsPage" class="wrapper"></div>
		
		<div class="info">Student Eval Edit Page</div>
		<div id="studentEvalEditPage" class="wrapper"></div>
		
		<div class="info">Student Eval Results Page</div>
		<div id="studentEvalResultsPage" class="wrapper"></div>
		<br></br>
		<br></br>
		<br></br>
		<br></br>
		<div class="info">Admin Page</div>
		<div id="adminPage" class="wrapper"></div>
	</body>
	
	<script type="text/javascript">
		$(document).ready(function(){
			$('head').append('<link rel=stylesheet href="/stylesheets/main.css" type="text/css"></link>');
			$('head').append('<link rel=stylesheet href="/stylesheets/evaluation.css" type="text/css"></link>');
			
			$('#coordHomePage').load("<%= Common.PAGE_COORD_HOME %> #frameBodyWrapper");
			$('#coordAddCoursePage').load("<%= Common.PAGE_COORD_COURSE %> #frameBodyWrapper");
			$('#coordEnrollPage').load("<%= Common.PAGE_COORD_COURSE_ENROLL %>?courseid=CS1101 #frameBodyWrapper");
			$('#coordCourseDetailsPage').load("<%= Common.PAGE_COORD_COURSE_DETAILS %>?courseid=CS2104 #frameBodyWrapper");
			$('#coordCourseStudentDetailsPage').load("<%= Common.PAGE_COORD_COURSE_STUDENT_DETAILS %>?courseid=CS2104&studentemail=teammates.test%40gmail.com #frameBodyWrapper");
			$('#coordCourseStudentEditPage').load("<%= Common.PAGE_COORD_COURSE_STUDENT_EDIT %>?courseid=CS2104&studentemail=benny.c.tmms%40gmail.com #frameBodyWrapper");
			$('#coordCourseEvalPage').load("<%= Common.PAGE_COORD_EVAL %> #frameBodyWrapper");
			$('#coordCourseEvalEditPage').load("<%= Common.PAGE_COORD_EVAL_EDIT %>?courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#coordCourseEvalResultsPage1').load("<%= Common.PAGE_COORD_EVAL_RESULTS %>?courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#coordCourseEvalResultsPage2').load("<%= Common.PAGE_COORD_EVAL_RESULTS %>?courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper", function(response, status, xml){
				$('#coordCourseEvalResultsPage2').find('#coordinatorEvaluationSummaryTable').hide();
				$('#coordCourseEvalResultsPage2').find('#coordinatorEvaluationDetailedReviewerTable').show();
			});
			$('#coordCourseEvalResultsPage3').load("<%= Common.PAGE_COORD_EVAL_RESULTS %>?courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper", function(response, status, xml){
				$('#coordCourseEvalResultsPage3').find('#coordinatorEvaluationSummaryTable').hide();
				$('#coordCourseEvalResultsPage3').find('#coordinatorEvaluationDetailedRevieweeTable').show();
			});
			$('#coordCourseEvalSubmissionViewPage').load("<%= Common.PAGE_COORD_EVAL_SUBMISSION_VIEW %>?courseid=CS2104&evaluationname=First+Eval&studentemail=benny.c.tmms%40gmail.com #frameBodyWrapper");		
			$('#coordCourseEvalSubmissionEditPage').load("<%= Common.PAGE_COORD_EVAL_SUBMISSION_EDIT %>?courseid=CS2104&evaluationname=First+Eval&studentemail=charlie.d.tmms%40gmail.com #frameBodyWrapper");
			
			$('#studentHomePage').load("<%= Common.PAGE_STUDENT_HOME %> #frameBodyWrapper");
			$('#studentCourseDetailsPage').load("<%= Common.PAGE_STUDENT_COURSE_DETAILS %>?courseid=CS2104 #frameBodyWrapper");
			$('#studentEvalEditPage').load("<%= Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT %>?courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#studentEvalResultsPage').load("<%= Common.PAGE_STUDENT_EVAL_RESULTS %>?courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			
			$('#adminPage').load("<%=Common.PAGE_ADMIN_HOME %> #frameBodyWrapper");
			
			
			//Tooltip
			initializetooltip();
			document.onmousemove = positiontip;
		});
	</script>
</html>