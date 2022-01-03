package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.result.Result;
import com.example.demo.service.ReportService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*")
@RestController("report")
@RequestMapping("/report")
public class reportController {
    @Autowired
    ReportService tmp;

    @Transactional
    @GetMapping("/getAllUnreadReports")
    @ApiGroup(group = {"admin","report"})
    @ApiOperation(value = "管理员获取所有未处理的举报")
    public Result getAllUnreadReports(){
        return tmp.getAllUnreadReports();
    }

    @Transactional
    @GetMapping("/getAllReportsByUser/{user_id}")
    @ApiGroup(group = {"report"})
    @ApiOperation(value = "用户获取自己所有提交的举报")
    public Result getAllReportsByUser(@PathVariable String user_id){
        return tmp.getAllReportsByUser(user_id);
    }

    @Transactional
    @PostMapping("/submitReport")
    @ApiGroup(group = {"good","report"})
    @ApiOperation(value = "提交举报",notes = "用户id，商品id，文字信息")
    public Result submitReport(@RequestParam("user_id") String user_id,
                               @RequestParam("good_id") String good_id,
                               @RequestParam("text") String text){
        return tmp.submitReport(user_id,good_id,text);
    }

    @Transactional
    @GetMapping("/readOneReport/{report_id}")
    @ApiGroup(group = {"admin","report"})
    @ApiOperation(value = "用户/管理员读取一条举报详细信息，report_state为0代表未处理，为1代表已处理",notes = "举报id")
    public Result readOneReport(@PathVariable String report_id){
        return tmp.readReport(report_id);
    }

    @Transactional
    @PostMapping("/finishReport")
    @ApiGroup(group = {"admin","report"})
    @ApiOperation(value = "管理员处理一条举报，plan_id输入”0”或“1”，0代表不处理，1代表下架商品",notes = "举报id，处理方案id")
    public Result finishReport(@RequestParam("report_id") String report_id,
                                  @RequestParam("plan_id") String plan_id){
        return tmp.finishReport(report_id,plan_id);
    }
}
