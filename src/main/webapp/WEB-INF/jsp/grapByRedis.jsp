<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>抢红包</title>
<!-- 加载Query文件-->
<script type="text/javascript"
	src="https://code.jquery.com/jquery-3.2.0.js">
        </script>
<script type="text/javascript">
            $(document).ready(function () {
           	 //模拟8000个异步请求，进行并发
              var max = 8000;
              for (var i = 1; i <= max; i++) {
	              $.post({
	                  //请求抢id为1的红包
	                  //根据自己请求修改对应的url和大红包编号
	                  url: "./userRedPacket/grapRedPacketByRedis?redPacketId=1&userId=" + i,
	                  //成功后的方法
	                  success: function (result) {
	                  }
	              });
                }
          });
        </script>
</head>
<body>
</body>
</html>