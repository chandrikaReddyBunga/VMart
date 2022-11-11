package vmart.example.mypc.vedasmart.helper;

public class Constants {
    public enum orderstatus {
        conformed("Confirmed"),
        cancel("Canceled");
        private final String name;

        orderstatus(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }

        public final String toString() {
            return this.name;
        }
    }

    public enum ordertype {
        self("Self Pick Up"),
        home("Home Delivery");
        private final String name;

        ordertype(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }

        public final String toString() {
            return this.name;
        }
    }

    public enum payment {
        onl("Online");
        private final String name;

        payment(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }

        public final String toString() {
            return this.name;
        }
    }
}
