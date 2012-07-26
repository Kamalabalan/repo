package teammates.logic;

import java.util.Arrays;
import java.util.logging.Logger;

import teammates.common.Common;

/**
 * This class represents an evaluation result for a given team.
 * It exposes the result via some public variables.
 */
public class TeamEvalResult {
	/** submitted value is uninitialized */
	public static int NA = Common.UNINITIALIZED_INT;
	/** submitted 'Not SUre' */
	public static int NSU = Common.POINTS_NOT_SURE;
	/** did Not SuBmit */
	public static int NSB = Common.POINTS_NOT_SUBMITTED;
	private static Logger log = Common.getLogger();

	/** submission values originally from students of the team */
	public int[][] claimed;
	/** submission values to be shown to coordinator (after normalization) */
	public int[][] normalizedClaimed;
	/** average perception of team shown to coord. Excludes self evaluations */
	public int[] normalizedAveragePerceived;
	/** team perception shown to students. denormalized to match their own claims */
	public int[][] denormalizedAveragePerceived;

	/** the values that were used to calculate normalizedAveragePerceived values*/
	public int[][] normalizedPeerContributionRatio;

	public TeamEvalResult(int[][] submissionValues) {

		log.fine("==================\n" + "starting result calculation for\n"
				+ pointsToString(submissionValues));

		claimed = submissionValues;

		normalizedClaimed = normalizeValues(claimed);

		int[][] claimedSanitized = sanitizeInput(submissionValues);
		log.fine("claimed values sanitized :\n"
				+ pointsToString(claimedSanitized));

		double[][] claimedSanitizedNormalized = normalizeValues(intToDouble(claimedSanitized));
		log.fine("claimed values sanitized and normalized :\n"
				+ pointsToString(claimedSanitizedNormalized));

		double[][] peerContributionRatioAsDouble = calculatePeerContributionRatio(claimedSanitizedNormalized);
		log.fine("peerContributionRatio as double :\n"
				+ pointsToString(peerContributionRatioAsDouble));

		double[] averagePerceivedAsDouble = averageColumns(peerContributionRatioAsDouble);
		log.fine("averagePerceived as double:\n"
				+ replaceMagicNumbers(Arrays.toString(averagePerceivedAsDouble)));

		double[] normalizedAveragePerceivedAsDouble = normalizeValues(averagePerceivedAsDouble);
		log.fine("normalizedAveragePerceivedAsDouble as double:\n"
				+ replaceMagicNumbers(Arrays
						.toString(normalizedAveragePerceivedAsDouble)));

		double[][] normalizedPeerContributionRatioAsDouble = adjustPeerContributionRatioToTallyNormalizedAveragePerceived(peerContributionRatioAsDouble);
		log.fine("normalizedPeerContributionRatio as double :\n"
				+ pointsToString(peerContributionRatioAsDouble));

		normalizedPeerContributionRatio = doubleToInt(normalizedPeerContributionRatioAsDouble);
		log.fine("normalizedUnbiasedClaimed as int :\n"
				+ pointsToString(normalizedPeerContributionRatio));

		denormalizedAveragePerceived = calculatePerceivedForStudents(
				claimedSanitized, normalizedAveragePerceivedAsDouble);
		log.fine("perceived to students :\n"
				+ pointsToString(denormalizedAveragePerceived));

		normalizedAveragePerceived = doubleToInt(normalizedAveragePerceivedAsDouble);

		log.fine("Final result:\n" + this.toString());

		log.fine("==================");
	}

	/**
	 * Replaces all missing points (for various reasons such as 'not sure' or
	 * 'did not submit') with NA
	 */
	private int[][] sanitizeInput(int[][] input) {
		int teamSize = input.length;
		int[][] output = new int[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			for (int j = 0; j < teamSize; j++) {
				int points = input[i][j];
				boolean pointsNotGiven = (points == Common.POINTS_NOT_SUBMITTED)
						|| (points == Common.POINTS_NOT_SURE);
				output[i][j] = pointsNotGiven ? NA : points;
			}
		}
		return output;
	}

	private static double[][] calculatePeerContributionRatio(double[][] input) {

		int teamSize = input.length;

		double[][] selfRatingsRemoved = removeSelfRatings(input);

		double[][] selfRatingRemovedAndNormalized = new double[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			selfRatingRemovedAndNormalized[i] = normalizeValues(selfRatingsRemoved[i]);
		}

		return selfRatingRemovedAndNormalized;
	}

	private static double[][] adjustPeerContributionRatioToTallyNormalizedAveragePerceived(
			double[][] peerContributionRatio) {
		double[] columnsAveraged = averageColumns(peerContributionRatio);
		double factor = calculateFactor(columnsAveraged);
		return multiplyByFactor(factor, peerContributionRatio);
	}

	private int[][] calculatePerceivedForStudents(int[][] claimedSanitized,
			double[] normalizedAveragePerceivedAsDouble) {
		int teamSize = claimedSanitized.length;
		int[][] output = new int[teamSize][teamSize];
		for (int k = 0; k < teamSize; k++) {
			output[k] = calculatePerceivedForStudent(claimedSanitized[k],
					normalizedAveragePerceivedAsDouble);
		}
		return output;
	}

	public static int[] calculatePerceivedForStudent(int[] claimedSanitizedRow,
			double[] normalizedAveragePerceivedAsDouble) {

		verify("Unsanitized value received ", isSanitized(claimedSanitizedRow));

		// remove from each array values matching special values in the other
		double[] filteredPerceived = purgeValuesCorrespondingToSpecialValuesInFilter(
				intToDouble(claimedSanitizedRow),
				normalizedAveragePerceivedAsDouble);
		int[] filteredSanitizedActual = doubleToInt(purgeValuesCorrespondingToSpecialValuesInFilter(
				normalizedAveragePerceivedAsDouble,
				intToDouble(claimedSanitizedRow)));

		double sumOfperceived = sum(filteredPerceived);
		double sumOfActual = sum(filteredSanitizedActual);

		// if the student did not submit
		if (sumOfActual == NA) {
			sumOfActual = sumOfperceived;
		}

		double factor = sumOfActual / sumOfperceived;

		return doubleToInt(multiplyByFactor(factor,
				normalizedAveragePerceivedAsDouble));
	}

	public static boolean isSanitized(int[] array) {
		for (int i = 0; i < array.length; i++) {
			if (!isSanitized(array[i])) {
				return false;
			}
		}
		return true;
	}

	private static boolean isSanitized(int i) {
		if (i == NSU) {
			return false;
		}
		if (i == NSB) {
			return false;
		}
		return true;
	}

	private static boolean isSpecialValue(int value) {
		return (value == NA) || (value == NSU) || (value == NSB);
	}

	private static double[][] multiplyByFactor(double factor, double[][] input) {
		int teamSize = input.length;
		double[][] output = new double[teamSize][teamSize];
		for (int i = 0; i < teamSize; i++) {
			output[i] = multiplyByFactor(factor, input[i]);
		}
		return output;
	}

	private static double[] multiplyByFactor(double factor, double[] input) {
		int teamSize = input.length;
		double[] output = new double[teamSize];
		for (int j = 0; j < teamSize; j++) {
			double value = input[j];
			if (!isSpecialValue((int) value)) {
				output[j] = (factor == 0 ? value : value * factor);
			} else {
				output[j] = value;
			}
		}
		return output;
	}

	public static double[] purgeValuesCorrespondingToSpecialValuesInFilter(
			double[] filterArray, double[] valueArray) {
		double[] returnValue = new double[filterArray.length];
		for (int i = 0; i < filterArray.length; i++) {
			int filterValue = (int) filterArray[i];
			boolean isSpecialValue = !isSanitized(filterValue)
					|| filterValue == NA;
			returnValue[i] = (isSpecialValue ? NA : valueArray[i]);
		}
		return returnValue;
	}

	public static double sum(double[] input) {
		double sum = NA;
		if (input.length == 0) {
			return 0;
		}

		verify("Unsanitized value in " + Arrays.toString(input),
				isSanitized(doubleToInt(input)));

		for (int i = 0; i < input.length; i++) {

			double value = input[i];
			if (value != NA) {
				sum = (sum == NA ? value : sum + value);
			}
		}
		return sum;
	}

	public static int sum(int[] input) {
		return (int) sum(intToDouble(input));
	}

	// TODO: methods like these private and use reflection to test
	public static double[][] removeSelfRatings(double[][] input) {
		double[][] output = new double[input.length][input.length];
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				output[i][j] = ((i == j) ? NA : input[i][j]);
			}
		}
		return output;
	}

	private static double[][] normalizeValues(double[][] input) {
		double[][] output = new double[input.length][input.length];
		for (int i = 0; i < input.length; i++) {
			output[i] = normalizeValues(input[i]);
		}
		return output;
	}

	// TODO: make this private and use reflection to test
	public static double[] normalizeValues(double[] input) {
		double factor = calculateFactor(input);
		return multiplyByFactor(factor, input);
	}

	public static double[] normalizeValues(int[] input) {
		return normalizeValues(intToDouble(input));
	}

	public static int[][] normalizeValues(int[][] input) {
		return doubleToInt(normalizeValues(intToDouble(input)));
	}

	private static double calculateFactor(double[] input) {
		double actualSum = 0;
		int count = 0;
		for (int j = 0; j < input.length; j++) {
			double value = input[j];
			int valueAsInt = (int) value;
			if (isSpecialValue(valueAsInt)) {
				continue;
			}
			actualSum += value;
			count++;
		}
	
		double idealSum = count * 100.0;
		double factor = actualSum == 0 ? 0 : idealSum / actualSum;
		log.fine("Factor = " + idealSum + "/" + actualSum + " = " + factor);
		return factor;
	}

	private static double[] intToDouble(int[] input) {
		double[] converted = new double[input.length];
		for (int i = 0; i < input.length; i++) {
			converted[i] = input[i];
		}
		return converted;
	}

	private static double[][] intToDouble(int[][] input) {
		double[][] converted = new double[input.length][input[0].length];
		for (int i = 0; i < input.length; i++) {
			converted[i] = intToDouble(input[i]);
		}
		return converted;
	}

	private static int[] doubleToInt(double[] input) {
		int[] converted = new int[input.length];
		for (int i = 0; i < input.length; i++) {
			converted[i] = (int) (Math.round(input[i]));
		}
		return converted;
	}

	private static int[][] doubleToInt(double[][] input) {
		int[][] output = new int[input.length][input.length];
		for (int i = 0; i < input.length; i++) {
			output[i] = doubleToInt(input[i]);
		}
		return output;
	}

	// TODO: make this private and use reflection to test
	public static double[] averageColumns(double[][] input) {
		double[] output = new double[input.length];

		for (int i = 0; i < input.length; i++) {
			verify("Unsanitized value in " + Arrays.toString(input[i]),
					isSanitized(doubleToInt(input[i])));
			output[i] = averageColumn(input, i);
		}
		log.fine("Column averages: "
				+ replaceMagicNumbers(Arrays.toString(output)));
		return output;
	}

	private static double averageColumn(double[][] array, int columnIndex) {
		double sum = 0;
		int count = 0;
		String values = "";
		for (int j = 0; j < array.length; j++) {
			double value = array[j][columnIndex];

			values = values + value + " ";
			if (value == NA) {
				continue;
			} else {
				sum += value;
				count++;
			}
		}
		// omit calculation if no data points
		double average = count == 0 ? NA : (double) (sum / count);

		String logMessage = "Average(" + values.trim() + ") = " + average;
		log.fine(replaceMagicNumbers(logMessage));

		return average;
	}

	public static String pointsToString(int[][] array) {
		return pointsToString(intToDouble(array)).replace(".0", "");
	}

	private String pointsToString(int[] input) {
		return replaceMagicNumbers(Arrays.toString(input)) + Common.EOL;
	}

	public static String pointsToString(double[][] array) {
		String returnValue = "";
		boolean isSquareArray = (array.length == array[0].length);
		int teamSize = (array.length - 1) / 3;
		int firstDividerLocation = teamSize - 1;
		int secondDividerLocation = teamSize * 2 - 1;
		int thirdDividerLocation = secondDividerLocation + 1;
		for (int i = 0; i < array.length; i++) {
			returnValue = returnValue + Arrays.toString(array[i]) + Common.EOL;
			if (isSquareArray) {
				continue;
			}
			if ((i == firstDividerLocation) || (i == secondDividerLocation)
					|| (i == thirdDividerLocation)) {
				returnValue = returnValue + "======================="
						+ Common.EOL;
			}
		}
		returnValue = replaceMagicNumbers(returnValue);
		return returnValue;
	}

	/** replaces 999 etc. with NA, NSB, NSU etc.
	 */
	public static String replaceMagicNumbers(String returnValue) {
		returnValue = returnValue.replace(NA + ".0", " NA");
		returnValue = returnValue.replace(NA + "", " NA");
		returnValue = returnValue.replace(NSB + ".0", "NSB");
		returnValue = returnValue.replace(NSU + ".0", "NSU");
		return returnValue;
	}

	public String toString() {
		return toString(0);
	}

	public String toString(int indent) {
		String indentString = Common.getIndent(indent);
		String divider = "======================" + Common.EOL;
		StringBuilder sb = new StringBuilder();
		sb.append("           claimed from student:");
		String filler = "                                ";
		sb.append(indentString
				+ pointsToString((claimed)).replace(Common.EOL,
						Common.EOL + indentString + filler));
		sb.append(divider);
		sb.append("              normalizedClaimed:");
		sb.append(indentString
				+ pointsToString((normalizedClaimed)).replace(Common.EOL,
						Common.EOL + indentString + filler));
		sb.append(divider);
		sb.append("normalizedPeerContributionRatio:");
		sb.append(indentString
				+ pointsToString(normalizedPeerContributionRatio).replace(
						Common.EOL, Common.EOL + indentString + filler));
		sb.append(divider);
		sb.append("     normalizedAveragePerceived:");
		sb.append(indentString
				+ pointsToString(normalizedAveragePerceived).replace(
						Common.EOL, Common.EOL + indentString + filler));
		sb.append(divider);

		sb.append("   denormalizedAveragePerceived:");
		sb.append(indentString
				+ pointsToString((denormalizedAveragePerceived)).replace(
						Common.EOL, Common.EOL + indentString + filler));
		sb.append(divider);
		return sb.toString();
	}

	private static void verify(String message, boolean condition) {
		// TODO: replace with assert?
		if (!condition) {
			throw new RuntimeException("Internal assertion failuer : "
					+ message);
		}
	}

}