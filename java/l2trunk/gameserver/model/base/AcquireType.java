package l2trunk.gameserver.model.base;

import static l2trunk.gameserver.model.base.ClassId.*;

public enum AcquireType {
    NORMAL,
    FISHING,
    CLAN,
    SUB_UNIT,
    TRANSFORMATION,
    CERTIFICATION,
    COLLECTION,
    TRANSFER_CARDINAL,
    TRANSFER_EVA_SAINTS,
    TRANSFER_SHILLIEN_SAINTS;

    public static final AcquireType[] VALUES = AcquireType.values();

    public static AcquireType transferType(ClassId classId) {
        switch (classId) {
            case cardinal:
                return TRANSFER_CARDINAL;
            case evaSaint:
                return TRANSFER_EVA_SAINTS;
            case shillienSaint:
                return TRANSFER_SHILLIEN_SAINTS;
        }

        return null;
    }

    public ClassId transferClassId() {
        switch (this) {
            case TRANSFER_CARDINAL:
                return cardinal;
            case TRANSFER_EVA_SAINTS:
                return evaSaint;
            case TRANSFER_SHILLIEN_SAINTS:
                return shillienSaint;
        }

        return null;
    }
}
