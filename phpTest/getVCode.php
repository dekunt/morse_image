<?php
	$phone = $_REQUEST['phone'];
	$action = $_REQUEST['action'];
	//$sqlPhone = mysql_escape_string($phone);
	$sqlPhone = $phone;
	$data = array();

	$con = mysql_connect("localhost","tanxyzco_dk","emp631763");
	if (!$con) {
		done(1);
	}
	mysql_select_db("tanxyzco_db", $con);
	$result = mysql_query("SELECT phone FROM user WHERE phone='".$sqlPhone."'");
	$tempExist = FALSE;
	while ($result && $row = mysql_fetch_array($result)) {
		if ($action == '1') { //注册
			done(201);
		}
		$tempExist = TRUE;
	}
	if ($action == '2' && !$tempExist) { //修改密码
		done(202);
	}

	$currentTime = time();
	$deadline = $currentTime - 60;
	$result2 = mysql_query("SELECT phone FROM v_code WHERE phone='".$sqlPhone."' AND createTime>$deadline");
	while ($result2 && $row = mysql_fetch_array($result2)){
		done(203);
	}

	$vCode = 1000;
	srand((double)microtime()*1000000);
	while (($vCode=rand()%10000) < 1000);//生成四位随机整数验证码

	$ch = curl_init();
	$sms = "【DK备忘录】你的验证码是 {$vCode} ，请在10分钟内使用";
	$sms = urlencode($sms);
	$url = 'http://apis.baidu.com/kingtto_media/106sms/106sms?mobile='.$phone.'&content='.$sms;
	$header = array('apikey: 67206896ee7907413b4f8f091e9cfad4');
	// 添加apikey到header
	curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	// 执行HTTP请求验证码
	curl_setopt($ch , CURLOPT_URL , $url);
	//$res = curl_exec($ch);
	//$resArray = json_decode($res);
	//if ($resArray == null || array_key_exists('errNum', $resArray)) {
	//	done(1);
	//}

	$currentTime = time();
	$deadline = $currentTime - 600;
	// 清除过期数据
	mysql_query("DELETE FROM v_code WHERE phone='".$sqlPhone."' OR createTime<=$deadline");
	// 数据库插入临时数据
	mysql_query("INSERT INTO v_code (phone, vCode, createTime) VALUES ('".$sqlPhone."', $vCode, $currentTime)");
	done(0);

	function done($errno)
	{
		global $con, $data;
		mysql_close($con);
		$msg = '';
		$status = 1;
		switch ($errno) {
			case 0: break;
			case 201: $status = 0; $msg = '该手机号已注册过了'; break;
			case 202: $status = 0; $msg = '手机号未注册'; break;
			case 203: $status = 0; $msg = '你的请求过于频繁，请稍候再试'; break;
			default: $status = 0; $msg = '数据错误，请稍候再试'; break;
		}
		$errmsg = array('errno' => $errno, 'msg' => $msg);
		$result = array('errmsg' => $errmsg, 'status' => $status, 'data' => $data);
		die(json_encode($result));
	}
?>