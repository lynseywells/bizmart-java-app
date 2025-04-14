package bizmart;

import statemachine.*;

/**
 * Main class of the Bizmart application. Initializes states in the state
 * machine and displays a JFrame with the initial state.
 */
public class Main {

    public static void main(String[] args) {
        State.setLoginState(new Login());
        State.setRegisterState(new Register());
        State.setPasswordState(new ForgotPassword());
        State.setCustomerState(new Customer());
        State.setEmployeeState(new Employee());
        State.setManagerState(new Manager());
        State.startApp();
    }
}
