import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.main.Main;

import java.util.Arrays;

public class Start {

    /**
     * To start the license, the -email and -pass option are required. Example: -license true -email yourEmail -pass yourPass The -name option is required to start the pirate. Example: -license false -name User228
     */
    public static void main(String[] args) throws AuthenticationException {
        Main.main(concat(new String[]{"--version", "1.12.2", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.12", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
