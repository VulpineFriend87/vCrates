package pro.vulpine.vCrates.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionChecker {

    private static final String root = "vcrates";

    public static boolean hasPermission(CommandSender sender, String base, String... parts) {

        if (sender.hasPermission(root + ".admin") || sender.hasPermission(root + ".*")) {

            return true;

        }

        String basePermission = root + "." + base.toLowerCase();

        if (parts == null || parts.length == 0) {

            if (sender.hasPermission(basePermission) || sender.hasPermission(basePermission + ".*")) {
                return true;
            }

            String permissionPrefix = basePermission + ".";
            for (PermissionAttachmentInfo pai : sender.getEffectivePermissions()) {

                if (pai.getValue() && pai.getPermission().startsWith(permissionPrefix)) {
                    return true;
                }

            }

        } else {

            if (sender.hasPermission(basePermission) || sender.hasPermission(basePermission + ".*")) {
                return true;
            }

            for (String part : parts) {

                String subPermission = basePermission + "." + part.toLowerCase();

                if (sender.hasPermission(subPermission) || sender.hasPermission(subPermission + ".*")) {
                    return true;
                }

            }

        }

        return false;

    }

}
