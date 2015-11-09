<?php
	$phone = $_REQUEST['phone'];
	$action = $_REQUEST['action'];
	$data = array();

    $pdo = null;
    try {
        $pdo = new PDO('mysql:host=localhost;dbname=tanxyzco_db;charset=utf8', 'tanxyzco_dk', 'emp631763');
        $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false); // 使用本地预处理（php5.3.6+默认为false）

		// 检查手机号
        if (!is_numeric($phone)) {
            done(207);
        }
        $query = $pdo->prepare("SELECT phone FROM user WHERE phone=?");
        if ($query->execute(array($phone)) && $row = $query->fetch()) {
            if ($action == '1') { //注册
				done(201);
			}
        }
        elseif ($action == '2') { //修改密码
			done(202);
        }

		$currentTime = time();
		$deadline = $currentTime - 60;
		// 检查60秒内的验证码
        $query = $pdo->prepare("SELECT phone FROM v_code WHERE phone=? AND createTime>?");
        if ($query->execute(array($phone, $deadline)) && $row = $query->fetch()) {
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
		$res = curl_exec($ch);
		$resArray = json_decode($res);
		if ($res == null || ($resArray != null && array_key_exists('errNum', $resArray))) {
			done(1);
		}

		$currentTime = time();
		$deadline = $currentTime - 600;
		// 清除过期数据
        $pdo->exec("DELETE FROM v_code WHERE createTime<=$deadline");
        if ($action != '1') {
        	$query = $pdo->prepare("DELETE FROM v_code WHERE phone=?");
        	$query->execute(array($phone));
        }
		// 数据库插入临时数据
        $query = $pdo->prepare("INSERT INTO v_code (phone, vCode, createTime) VALUES (?, ?, ?)");
        $query->execute(array($phone, $vCode, $currentTime));
		done(0);

    } catch (PDOException $e) {
        done(1);
    }

	function done($errno)
	{
        global $pdo, $data;
        $pdo = null;
		$msg = '';
		$status = 1;
		switch ($errno) {
			case 0: break;
			case 201: $status = 0; $msg = '该手机号已注册过了'; break;
			case 202: $status = 0; $msg = '手机号未注册'; break;
			case 203: $status = 0; $msg = '你的请求过于频繁，请稍候再试'; break;
            case 207: $status = 0; $msg = '手机号格式不对'; break;
			default: $status = 0; $msg = '数据错误，请稍候再试'; break;
		}
		$errmsg = array('errno' => $errno, 'msg' => $msg);
		$result = array('errmsg' => $errmsg, 'status' => $status, 'data' => $data);
		die(json_encode($result));
	}
?>