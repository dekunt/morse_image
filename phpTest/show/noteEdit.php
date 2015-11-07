<?php
    $noteId = $_REQUEST['noteId'];
    $title = $_REQUEST['title'];
    $content = $_REQUEST['content'];
    $uid = $_REQUEST['uid'];
    $hash = $_REQUEST['hash'];
    $data = array();
    
    $pdo = null;
    if (strlen($title) == 0 && strlen($content)) {
        done(401);
    }
    try {
        $pdo = new PDO('mysql:host=localhost;dbname=tanxyzco_db;charset=utf8', 'tanxyzco_dk', 'emp631763');
        $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false); // 使用本地预处理（php5.3.6+默认为false）

        // 检查用户
        $query = $pdo->prepare("SELECT * FROM user WHERE uid=?");
        if ($query->execute(array($uid)) && $row = $query->fetch()) {
            if ($row['hash'] != $hash) {
                done(100);
            }
        }
        else {
            done(101);
        }

        // 检查noteId
        $query = $pdo->prepare("SELECT noteId FROM note WHERE noteId=?");
        if ($query->execute(array($noteId)) && $row = $query->fetch()) {
            onGotNote($row);
        }
        else {
            done(402);
        }

    } catch (PDOException $e) {
        done(1);
    }

    function onGotNote($note)
    {
        global $pdo, $data, $title, $content;
        $noteId = $note['noteId'];
        $currentTime = time();
        $query = $pdo->prepare("UPDATE note SET title=?, content=?, modifyTime=? WHERE noteId=?");
        if ($query->execute(array($title, $content, $currentTime, $noteId))) {
            done(0);
        }
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
            case 401: $status = 0; $msg = '内容不能为空'; break;
            case 402: $status = 0; $msg = '该条目不存在'; break;
            case 100: $status = 0; $msg = '登录信息已过期'; break;
            case 101: $status = 0; $msg = '用户不存在'; break;
            default: $status = 0; $msg = '数据错误'; break;
        }
        $errmsg = array('errno' => $errno, 'msg' => $msg);
        $result = array('errmsg' => $errmsg, 'status' => $status, 'data' => $data);
        die(json_encode($result));
    }
?>