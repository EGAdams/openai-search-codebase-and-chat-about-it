package com.nac.utils;

import com.nac.configs.MeasureConfig;
import com.nac.model.Program;
import com.nac.model.Run;
import com.nac.model.Test;

import java.text.DateFormat;
import java.text.Format;
import java.util.Date;

/**
 * Created by andreikaralkou on 2/12/14.
 */
public class ReportGenerator {
    private static final String HTML_HEAD =
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" +
                    "\r\n" +
                    "<head>\r\n" +
                    "<meta http-equiv=\"Content-Language\" content=\"en-us\" />\r\n" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n" +
                    "<title>Test Date</title>\r\n" +
                    "<style type=\"text/css\">\r\n" +
                    "body\r\n" +
                    "{\r\n" +
                    "  font-family: Arial, Helvetica, sans-serif;\r\n" +
                    "  font-size: 11pt;\r\n" +
                    "}\r\n" +
                    "\r\n" +
                    "h1\r\n" +
                    "{\r\n" +
                    "  color: blue;\r\n" +
                    "  margin-bottom: 0;\r\n" +
                    "}\r\n" +
                    "\r\n" +
                    "h2\r\n" +
                    "{\r\n" +
                    "  color: navy;\r\n" +
                    "  margin-top: 0;\r\n" +
                    "  margin-bottom: 1em;\r\n" +
                    "}\r\n" +
                    "\r\n" +
                    "h3\r\n" +
                    "{\r\n" +
                    "  color: teal;\r\n" +
                    "  margin-top: 1em;\r\n" +
                    "  margin-bottom: 1em;\r\n" +
                    "}\r\n" +
                    "\r\n" +
                    ".style1 {\r\n" +
                    "   text-align: center;\r\n" +
                    "}\r\n" +
                    "\r\n" +
                    ".style2 {\r\n" +
                    "   font-size: large;\r\n" +
                    "}\r\n" +
                    "\r\n" +
                    ".style3 {\r\n" +
                    "   font-size: large;\r\n" +
                    "   border-bottom: medium;\r\n" +
                    "}\r\n" +
                    "\r\n" +
                    "</style>\r\n" +
                    "</head>\r\n" +
                    "\r\n" +
                    "<body>\r\n" +
                    "\r\n" +
                    "<h1>NAC Dynamics</h1>\r\n" +
                    "<h2>Dynamic Friction Decelerometer Report</h2>\r\n" +
                    "\r\n" +
                    "<table width=\"1000\">\r\n" +
                    "    <tr>\r\n" +
                    "        <td style=\"width: 161px\"><em>Test Date</em></td>\r\n" +
                    "        <td>*TESTDATE*</td>\r\n" +
                    "    </tr>\r\n" +
                    "    <tr>\r\n" +
                    "        <td style=\"width: 161px\"><em>Device ID</em></td>\r\n" +
                    "        <td>*DECELID*</td>\r\n" +
                    "    </tr>\r\n" +
                    "    <tr>\r\n" +
                    "        <td style=\"width: 161px\"><em>Operator</em></td>\r\n" +
                    "        <td>*OPERATOR*</td>\r\n" +
                    "    </tr>\r\n" +
                    "    <tr>\r\n" +
                    "        <td style=\"width: 161px\"><em>Airport</em></td>\r\n" +
                    "        <td>*AIRPORT*</td>\r\n" +
                    "    </tr>\r\n" +
                    "    <tr>\r\n" +
                    "        <td style=\"width: 161px\"><em>Location of Test</em></td>\r\n" +
                    "        <td>*LOCATION*</td>\r\n" +
                    "    </tr>\r\n" +
                    "    <tr>\r\n" +
                    "        <td style=\"width: 161px\"><em>Offset</em></td>\r\n" +
                    "        <td>*OFFSET*</td>\r\n" +
                    "    </tr>\r\n" +
                    "    <tr>\r\n" +
                    "        <td style=\"width: 161px\">&nbsp;</td>\r\n" +
                    "        <td>&nbsp;</td>\r\n" +
                    "    </tr>\r\n" +
                    "    <tr>\r\n" +
                    "        <td style=\"width: 161px\"><em>Test Speed</em></td>\r\n" +
                    "        <td>*TESTSPEED*</td>\r\n" +
                    "    </tr>\r\n" +
                    "    <tr>\r\n" +
                    "        <td style=\"width: 161px\"><em>Number of Tests</em></td>\r\n" +
                    "        <td>*TESTCOUNT*</td>\r\n" +
                    "    </tr>\r\n" +
                    "</table>\r\n" +
                    "\r\n" +
                    "<p><em>Total Average Braking Action:</em> <strong>*TOTALG*</strong></p>\r\n" +
                    "\r\n" +
                    "<table style=\"width: 310px\">\r\n" +
                    "   <tr>\r\n" +
                    "       <td style=\"width: 126px\" class=\"style1\"><em>Average Friction</em></td>\r\n" +
                    "       <td>&nbsp;</td>\r\n" +
                    "   </tr>\r\n" +
                    "   <tr>\r\n" +
                    "       <td style=\"width: 126px\" class=\"style1\">1/3</td>\r\n" +
                    "       <td>*FIRSTG*</td>\r\n" +
                    "   </tr>\r\n" +
                    "   <tr>\r\n" +
                    "       <td style=\"width: 126px\" class=\"style1\">2/3</td>\r\n" +
                    "       <td>*SECONDG*</td>\r\n" +
                    "   </tr>\r\n" +
                    "   <tr>\r\n" +
                    "       <td style=\"width: 126px\" class=\"style1\">3/3</td>\r\n" +
                    "       <td>*THIRDG*</td>\r\n" +
                    "   </tr>\r\n" +
                    "   <tr>\r\n" +
                    "       <td style=\"width: 126px\" class=\"style1\">&nbsp;</td>\r\n" +
                    "       <td>&nbsp;</td>\r\n" +
                    "   </tr>\r\n" +
                    "</table>\r\n" +
                    "\r\n" +
                    "<table style=\"width: 100%\">\r\n" +
                    "   <tr>\r\n" +
                    "       <td class=\"style1\" style=\"width: 50px\" valign=\"bottom\"><em>Test</em></td>\r\n" +
                    "       <td class=\"style1\" style=\"width: 100px\" valign=\"bottom\"><em>Braking<strong><br />\r\n" +
                    "       </strong>Action</em></td>\r\n" +
                    "       <td style=\"width: 200px\" valign=\"bottom\"><em>Date &amp; Time</em></td>\r\n" +
                    "       <td style=\"width: *\" valign=\"bottom\"><em>Contaminate</em></td>\r\n" +
                    "       </tr>\r\n";

    private static final String HTML_DETAIL =
            "   <tr>\r\n" +
                    "       <td class=\"style1\" style=\"width: 50px\">*TESTNO*</td>\r\n" +
                    "       <td class=\"style1\" style=\"width: 100px\">*BRAKINGACTION*</td>\r\n" +
                    "       <td style=\"width: 200px\">*DATETIME*</td>\r\n" +
                    "       <td style=\"width: *\">*CONDITION*</td>\r\n" +
                    "   </tr>\r\n";

    private static final String HTML_TAIL =
            "</table>\r\n" +
                    "\r\n" +
                    "<p>&nbsp;</p>\r\n" +
                    "<table style=\"width: 100%\">\r\n" +
                    "   <tr>\r\n" +
                    "       <td style=\"width: 185px\" class=\"style3\">FAA NOTAM Number</td>\r\n" +
                    "       <td class=\"style2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;</td>\r\n" +
                    "       <td class=\"style3\">&nbsp;</td>\r\n" +
                    "       <td class=\"style3\" style=\"width: 91px\">Issued by</td>\r\n" +
                    "       <td class=\"style3\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;</td>\r\n" +
                    "   </tr>\r\n" +
                    "</table>\r\n" +
                    "\r\n" +
                    "<p>&nbsp;</p>\r\n" +
                    "<p class=\"style2\">Signed: </p>\r\n" +
                    "\r\n" +
                    "</body>\r\n" +
                    "\r\n" +
                    "</html>\r\n";

    public static String generateReport(String uuid, MeasureConfig config, Program program, Test test, Format dateFormat, Format timeFormat) {
        StringBuilder report = new StringBuilder();
        String forceUnitString = config.getForceUnitsString();
        int avg23 = Math.round(test.getAverage23());
        int avg33 = Math.round(test.getAverage33());
        String avg23String = avg23 > 0 ? String.valueOf(avg23) : "NaN";
        String avg33String = avg33 > 0 ? String.valueOf(avg33) : "NaN";
        Date date = new Date(test.getTestDate());
        String head = HTML_HEAD.
                replace("*TESTDATE*", dateFormat.format(date) + " " + timeFormat.format(date)).
                replace("*DECELID*", uuid).
                replace("*OPERATOR*", test.getOperator()).
                replace("*AIRPORT*", program.getAirportName()).
                replace("*LOCATION*", program.getLocation()).
                replace("*OFFSET*", test.getOffset()).
                replace("*TESTSPEED*", Math.round(test.getTestSpeed()) + config.getMeasurementUnitsString()).
                replace("*TESTCOUNT*", String.valueOf(test.getRunList().size())).
                replace("*TOTALG*", Math.round(test.getAverage()) + forceUnitString).
                replace("*FIRSTG*", Math.round(test.getAverage13()) + forceUnitString).
                replace("*SECONDG*", avg23String + forceUnitString).
                replace("*THIRDG*", avg33String + forceUnitString);

        report.append(head);

        int testNumber = 1;
        for (Run run : test.getRunList()) {
            Date runDate = new Date(run.getDate());
            String detail = HTML_DETAIL.
                    replace("*TESTNO*", String.valueOf(testNumber)).
                    replace("*BRAKINGACTION*", Math.round(run.getValue()) + forceUnitString).
                    replace("*DATETIME*", dateFormat.format(runDate) + " " + timeFormat.format(runDate)).
                    replace("*CONDITION*", run.getCondition());
            report.append(detail);
            testNumber++;
        }

        report.append(HTML_TAIL);

        return report.toString();
    }
}
