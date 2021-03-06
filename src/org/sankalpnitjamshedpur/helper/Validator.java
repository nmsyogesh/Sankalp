package org.sankalpnitjamshedpur.helper;

public class Validator {

	public static boolean validateEmail(String email)
			throws ValidationException {
		if (email == null || email.trim().equalsIgnoreCase("")) {

			throw new ValidationException("Email Id cann't be blank!!");
		} else if (!email
				.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,4})$")) {
			throw new ValidationException("Envalid Email Id!!");
		}
		return true;
	}

	public static boolean validatePassword(String password)
			throws ValidationException {
		if (password == null || password.trim().equalsIgnoreCase("")) {
			throw new ValidationException("Password cann't be blank!!");
		} else if (!password
				.matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})")) {
			throw new ValidationException(
					"Password should contain Minimum 8 characters, at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number");
		}
		return true;
	}

	public static boolean validateMobileNo(String mobile)
			throws ValidationException {
		if (mobile == null || mobile.trim().equalsIgnoreCase("")) {
			throw new ValidationException("Mobile No cann't be blank!!");
		} else if (!mobile.matches("(\\d{10})")) {
			throw new ValidationException(
					"Mobile No should be of 10 digits only!!");
		}
		return true;
	}

	public static boolean validateRollNo(String rollNo)
			throws ValidationException {
		if (rollNo == null || rollNo.trim().equalsIgnoreCase("")) {
			throw new ValidationException("Roll No cann't be blank!!");
		} else {
			try {
				int roll = Integer.parseInt(rollNo);
				if (roll < 1 || roll > 999) {
					throw new ValidationException(
							"Roll No must be in range 1-999");
				}
			} catch (NumberFormatException e) {
				throw new ValidationException("Roll No should be Numeric!!");
			}
		}
		return true;
	}

	public static boolean validateVolunteerId(String volunteerId)
			throws ValidationException {
		if (volunteerId == null || volunteerId.trim().equalsIgnoreCase("")) {
			throw new ValidationException("VolunteerId cann't be blank!!");
		} else if (!volunteerId
				.matches("^201[2-9](?i)((EC)|(CS)|(EL)|(ME)|(MT)|(CE)|(PR)|(MCA))(?-i)[0-9][0-9][0-9]$")) {
			throw new ValidationException("Invalid Volunteer Id!!");
		}
		return true;
	}
}
