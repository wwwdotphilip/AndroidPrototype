package co.familyplay.androidprototype.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Check if the enter email is a valid email.
// to use this call fist initialize the class by calling
//EmailValidator val = new EmailValidator()
//then create an if statement to compare
//        if (val.validate("name@email.com")) {
//             do something there
//        }
public class EmailValidator {
    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public EmailValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    public boolean validate(final String hex) {

        matcher = pattern.matcher(hex);
        return matcher.matches();

    }

}
