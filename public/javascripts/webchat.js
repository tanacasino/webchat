/*
 * webchat.js
 */

/** Namespace **/
var webchat;
if (!webchat) {
    webchat = {};
}

/** Functions **/
webchat.formatDate = function(timestamp) {
    var df = new DateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    var d = new Date;
    d.setTime(timestamp);
    return df.format(d);
}

/** Classes **/
webchat.Chat = function() {};

webchat.Chat.prototype.connectWebSocket = function(username, websocketURL, avatarURL) {
    var socket = new WebSocket(websocketURL);
    socket.onmessage = function(event) {
        var json = $.parseJSON(event.data);
        if (json.type == 'new_message') {
            var datestring = webchat.formatDate(json.timestamp);
            var text = $('<div>').text(json.text).html(); // escape html
            var msg = {
                mid: json.mid,
                name: json.username,
                fullname: json.fullname,
                text: text,
                date: datestring
            };
            var room_id = json.room;
            $("#chatMessageTemplate").tmpl(msg).prependTo("#messages-room-" + room_id);
            $("#message-id-" + json.mid).fadeIn(1500);
            // Notification
            if (username != json.username) { // Owned message.
                if (window.webkitNotifications.checkPermission() == 0) {
                    var icon = avatarURL + json.username;
                    var room_name = $('#room-name-' + room_id).text();
                    var title = '@' + json.username + " : " + room_name;
                    var message = json.text;
                    var notification = window.webkitNotifications.createNotification(icon, title, message);
                    notification.ondisplay = function() {
                        setTimeout(function() {
                            notification.cancel();
                        }, 5000);
                    };
                    notification.show();
                }
            }
        } else if (json.type == 'online') {
            // TODO
        } else if (json.type == 'offline') {
            // TODO
        } else if (json.type == 'delete_message') {
            // TODO
        } else if (json.type == 'update_message') {
            // TODO
        } else if (json.type == 'new_file') {
            // TODO
        } else if (json.type == 'delete_file') {
            // TODO
        }
    }
    return socket;
}

webchat.Chat.prototype.setupSendMessage = function(ws) {
    // Send Message
    $('div.main-content a.send').each(function(i, element) {
        $(this).bind('click', function(e) {
            var room_id = $('#main-container div.main-content').filter(function(index) {
                return $(this).css('display') == 'block';
            }).first().attr('id');
            var textarea_id = '#' + room_id + '-text';
            var message = $(textarea_id).val().trim();
            $(textarea_id).val('');
            if (message == "") {
                return;
            }
            var request = {
                type: "new_message",
                text: message,
                room: room_id.replace("room-", "")
                };
            ws.send(JSON.stringify(request));
        });
    });
}

webchat.Chat.prototype.setupSendShortcut = function() {
    // Send Message Shortcut. Shift + Enter
    $('div.main-content textarea.new_message').each(function(i, element) {
        $(this).bind('keypress', function(e) {
            if((e.charCode == 13 || e.keyCode == 13) && e.shiftKey) {
                $('div.main-content a.send:first').click();
                e.preventDefault()
            }
        })
    });
}

// Read more messages.
webchat.Chat.prototype.setupReadMore = function(readMoreURL) {
    $('div.main-content a.read-more-messages').each(function(i, element) {
        $(this).bind('click', function(e) {
            // Find room id.
            var room_id = $('#main-container div.main-content').filter(function(index) {
                return $(this).css('display') == 'block';
            }).first().attr('id').replace("room-", "");
            // Find last message id.
            var lastMessageId = $("#messages-room-" + room_id + " .chat_msg:last").attr('id').replace("message-id-", "");
            var url = readMoreURL + "?message=" + lastMessageId + "&room=" + room_id;
            $.getJSON(url, function(data) {
                $.each(data, function(key, val) {
                    var datestring = webchat.formatDate(val.timestamp);
                    var text = $('<div>').text(val.message).html(); // escape html
                    var msg = {
                        mid: val.id,
                        name: val.username,
                        text: val.message,
                        fullname: val.fullname,
                        date: datestring
                    };
                    $("#chatMessageTemplate").tmpl(msg).appendTo("#messages-room-" + room_id);
                    $("#message-id-" + msg.mid).fadeIn(1500);
                });
            });
        })
    });
}

webchat.Chat.prototype.createTabMenu = function(url) {
    var mainContainer = $('#main-container .main-content');
    mainContainer.hide().filter(':first').show();
    $('#tab-navigation a').click(function() {
        mainContainer.hide();
        mainContainer.filter(this.hash).show();
        $('#tab-navigation a').removeClass('active');
        $(this).addClass('active');
        return false;
    });
}

