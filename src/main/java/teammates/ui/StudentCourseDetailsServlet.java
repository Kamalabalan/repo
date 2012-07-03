package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.TeamData;

@SuppressWarnings("serial")
/**
 * Servlet to handle student details (showing team members, and in future for Team Forming as well)
 * @author Aldrian Obaja
 *
 */
public class StudentCourseDetailsServlet extends ActionServlet<StudentCourseDetailsHelper> {

	@Override
	protected StudentCourseDetailsHelper instantiateHelper() {
		return new StudentCourseDetailsHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, StudentCourseDetailsHelper helper)
			throws EntityDoesNotExistException {
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		if(courseID==null){
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			return;
		}
		
		helper.course = helper.server.getCourseDetails(courseID);
		helper.coordName = helper.server.getCoord(helper.course.coord).name;
		helper.student = helper.server.getStudentInCourseForGoogleId(courseID, helper.userId);
		helper.team = getTeam(helper.server.getTeamsForCourse(courseID),helper.student);
	}
	
	/**
	 * Returns the TeamData object of the student in the course
	 * @param course
	 * @param student
	 * @return
	 */
	private TeamData getTeam(CourseData course, StudentData student){
		for(TeamData team: course.teams){
			if(team.name.equals(student.team)){
				return team;
			}
		}
		return null;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_COURSE_DETAILS;
	}

}