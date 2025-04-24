package io.github.Hayo87.type;

public enum FilterType {
    SYNONYM,
    HIDER;

    public boolean isPre() {
        return switch (this) {
            case SYNONYM -> true;
            case HIDER -> false;
        };
    }

    public boolean isPost() {
        return !isPre();
    }
}