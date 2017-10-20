package iris.jaagore.sabita_sant.alarm;

/**
 * Created by Sud on 10/13/17.
 */

public class Arithematic {
    int num1, num2, result;
    final char op[] = {'+', '-', '*', '/'};
    char operator;


    private int calResult(int r) {
        int result;
        switch (r) {
            case 0:
                result = num1 + num2;
                break;
            case 1:
                result = num1 - num2;
                break;
            case 2:
                result = num1 * num2;
                break;
            case 3:
                result = num1 / num2;
                break;
            default:
                result = 0;
        }
        return result;
    }

    public int getNum1() {
        num1 = (int) ((Math.random() * 100) % 23) + 1;
        return num1;
    }

    public int getNum2() {
        num2 = (int) ((Math.random() * 100) % num1) + 1;
        return num2;
    }

    public char getOperator() {
        int r = (int) (Math.random() * 10 % 4);
        operator = op[r];
        result=calResult(r);
        return operator;
    }

    public int getResult() {


        return result;
    }
}
