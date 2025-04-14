package model;

public class Testrail {
    public enum AutomationStatus {

        DONE(1),
        NOT_YET(2),
        DO_AGAIN(7);

        private int code;

        AutomationStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public enum AutomationType {

        NO(1),
        YES(2),
        TBR(3);

        private int code;

        AutomationType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public enum TestCaseStatus {

        APPROVED(3);

        private int code;

        TestCaseStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
