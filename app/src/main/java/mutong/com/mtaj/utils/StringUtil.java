package mutong.com.mtaj.utils;

public class StringUtil
{
    public static boolean isEmpty(String str)
    {
        if(str == null || str.length() <= 0)
        {
            return true;
        }
        return false;
    }

    public static String addSpace(String num)
    {
        StringBuffer sb = new StringBuffer();
        if(!isEmpty(num) && num.length() == 11)
        {
            sb.append(num.substring(0,3)).append(" ")
                    .append(num.substring(3,7)).append(" ")
                    .append(num.substring(7,11));

            return sb.toString();
        }
        return "error";
    }
}
