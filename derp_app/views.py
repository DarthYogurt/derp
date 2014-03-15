# -*- coding: utf-8 -*-
import datetime
import json
import math
import os
import random
import smtplib
import sys

from django.core.paginator import Paginator, PageNotAnInteger, EmptyPage
from django.http.response import HttpResponse
from django.shortcuts import render
from django.template.context import Context
from django.template.loader import get_template
from django.views.decorators.csrf import csrf_exempt

from derp_app.models import *


@csrf_exempt
def login(request):
    
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    if User.objects.filter(fbId=data.get('fbUserId',0)).exists():
        existUser = User.objects.get(fbId=data.get('fbUserId',1))
        existUser.fbName = data.get("fbUserName", "").encode("utf-8")
        existUser.activated = True
        existUser.save()
    else:
        newUser = User(
                       fbId = data.get('fbUserId',0),
                       fbName = data.get("fbUserName", "").encode("utf-8"),
                       activated = True,                   
                       )
        newUser.save()
    
    for friend in data['fbFriends']:
        fbName = friend['fbName'].encode("utf-8") #.encode("utf-8") #.encode("utf-8")   
 
        user = None
        if User.objects.filter(fbId=friend.get('fbId',0)).exists():

            user = User.objects.get(fbId=friend.get('fbId',0))
            user.fbName = fbName #friend.get("fbName",0)
            user.save()
        else:
            user = User(
                           fbId=friend.get("fbId",0),
                           fbName= fbName, #friend.get("fbName",0),
                           )
            user.save()
   
        if not Friend.objects.filter(parentFriend = User.objects.get(fbId=data.get('fbUserId',0)), 
                                 friendId = user).exists():
            newFriendModel = Friend(
                                   parentFriend = User.objects.get(fbId=data.get('fbUserId',1)),
                                   friendId = user
                                   )
            newFriendModel.save()
    
    userId = User.objects.get(fbId=data.get("fbUserId",1))
    return HttpResponse(userId.id)

@csrf_exempt
def uploadPic(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    #for d in data:
    #    print d,data[d]
    
    #print request.FILES['image']
    
    newPicture = Picture(
                         targetId = User.objects.get(fbId=data.get("targetFbId", 1)),
                         posterId = User.objects.get(fbId=data.get("fbUserId",1)),
                         image = request.FILES['image'],
                         caption = data.get("caption", 0),
                         title = data.get("title",""),
                         date = datetime.datetime.now()
                         )
    newPicture.save()
    
    
    #print Notification.objects.filter(targetUser=User.objects.get(fbId=data.get("targetFbId", 1))).exists()
    if not Notification.objects.filter(targetUser=User.objects.get(fbId=data.get("targetFbId", 1))).exists():
        
        newNotification = Notification(
                                       targetUser = User.objects.get(fbId=data.get("targetFbId", 1)),
                                       poster = User.objects.get(fbId=data.get("fbUserId",1)),
                                       picture = newPicture,
                                       type = "picture",
                                       text = User.objects.get(fbId=data.get("fbUserId", 1)).fbName + " has Derped you, click to see"
                                       )
        newNotification.save()
    
    j={}
    j['imageUrl'] = str(newPicture.image)
    j['picId'] = str(newPicture.id)
    
    return HttpResponse(json.dumps(j), content_type="application/json")

def externalPicView(request,picId):
    
    pic = Picture.objects.get(id=picId)
    
    variables = {}
    variables['friend'] = pic.posterId.fbName
    variables['you'] = pic.targetId.fbName
    variables['caption'] = pic.caption
    variables['title'] = pic.title
    variables['imageUrl'] = str(request.get_host()) + str(pic.image) 
    
    t = get_template('external-view.html')
    c = Context(variables)
    return HttpResponse(t.render(c))


def getUserId(request,fbUserId):
    try:
        return HttpResponse(User.objects.get(fbId=fbUserId).id)
    except:
        return HttpResponse("User does not exist")
    
@csrf_exempt
def getPic(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    picId = data.get("picId", 1)
    userFbId = data.get("fbUserId", 1)
    
    j={}
    pic = None

    if int(picId) == 0: 
        totalPics = Picture.objects.all().count()
        if totalPics > 0:
            
            picId = random.randint(1,totalPics)
            pic = Picture.objects.all()[picId-1]
        else:
            HttpResponse("no pictures in ")
    else:
        pic = Picture.objects.get(id = picId)
        
    
    j['picId'] = pic.id
    j['targetUserId'] = str(pic.targetId.id)
    j['targetFbId'] = str(pic.targetId.fbId)
    j['targetFbName'] = str(pic.targetId.fbName)
    j['posterUserId'] = str(pic.posterId.id)
    j['posterFbId'] = str(pic.posterId.fbId)
    j['posterFbName'] = str(pic.posterId.fbName)
    j['imageUrl'] = request.get_host() + str(pic.image)
    j['caption'] = pic.caption
    j['views'] = pic.views
    j['upVote'] = pic.upVote
    j['downVote'] = pic.downVote
    j['title'] = pic.title
    j['popularity'] = str(pic.popularity)
    
#     print Picture.objects.get(id=pic.id)
#     print User.objects.get(fbId = userFbId)
    j['userVotedUp'] = ""
    if Vote.objects.filter(user = User.objects.get(fbId= userFbId), picture = Picture.objects.get(id=pic.id)).exists():
        try:
            j['userVotedUp'] = str(Vote.objects.get(user = User.objects.get(fbId= userFbId), 
                            picture = Picture.objects.get(id=pic.id)).voteUp)
        except:
            j['userVotedUp'] = ""
    
    j['comments'] = []

    for comment in Comment.objects.filter(picture=Picture.objects.get(id=picId)):
       
        com = {}
        com['posterId'] = comment.poster.id
        com['posterFbId'] = str(comment.poster.fbId)
        com['posterFbName'] = str(comment.poster.fbName)
        com['comment'] = comment.comment
       # com['commentTime'] = comment.timeModified   # has problem cannot json serialize
        j['comments'].append(com)
    return HttpResponse(json.dumps(j), content_type="application/json")

def getTeamGallery(request,fbId):
    user = User.objects.get(fbId=fbId)
    pictures = Picture.objects.filter(targetId = user).order_by("-date")
    j={}
    j['targetId'] = user.id
    j['targetFbId'] = str(user.fbId)
    j['targetFbName'] = str(user.fbName) 
    j['teamGallery'] =[]
    for p in pictures:
        temp = {}
        temp['picId'] = p.id
        #temp['teamId'] = p.posterId.id
        temp['posterId'] = p.posterId.id
        temp['posterFbId'] = str(p.posterId.fbId)
        temp['posterFbName'] = str(p.posterId.fbName)
        temp['popularity'] = p.popularity
        temp['upVote'] = p.upVote
        temp['downVote'] = p.downVote
        temp['views'] = p.views
        temp['imageUrl'] = str(request.get_host()) + str(p.image)
        temp['caption'] = p.caption
        temp['title'] = p.title
        
        temp['comments'] = []

        for comment in Comment.objects.filter(picture=Picture.objects.get(id=p.id)):
            com = {}
            com['posterId'] = comment.poster.id
            com['posterFbId'] = str(comment.poster.fbId)
            com['posterFbName'] = str(comment.poster.fbName)
            com['comment'] = comment.comment
            # com['commentTime'] = comment.timeModified   # has problem cannot json serialize
            temp['comments'].append(com)
#         
        
        j['teamGallery'].append(temp)
        
        
    return HttpResponse(json.dumps(j), content_type="application/json")

def gallery(request, numPerPage, pageNum):     
    picture_list = Picture.objects.order_by("-date")
    paginator = Paginator(picture_list, numPerPage)
    page = pageNum
    totalPages = int(math.ceil(picture_list.count()/float(numPerPage)))
    
    try:
        pictures = paginator.page(page)
    except PageNotAnInteger:
        pictures = paginator.page(1)
    except EmptyPage:
        pictures = paginator.page(paginator.num_pages)
    
    j={}
    j['totalPages'] = totalPages
    j['onPage'] = page
    j['gallery'] = []
    for p in pictures:
        pic = {}
        pic['imageUrl'] = str(request.get_host()) + str(p.image)
        pic['picId'] = p.id    
        j['gallery'].append(pic)
    
    return HttpResponse(json.dumps(j), content_type="application/json")

@csrf_exempt
def addComment(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    #for d in data:
    #    print d,data[d]
#     data = {}
#     
#     data['picId'] = 11
#     data['posterFbId'] = 606082631
#     data['comment'] = "testing comment"
    
    
    pic = Picture.objects.get(id = data.get("picId", 1))
    poster = User.objects.get(fbId = data.get("posterFbId",1))
    
    newComment = Comment(
                     picture = pic,
                     poster = poster,
                     comment = data.get("comment","").encode("utf-8"),
                     timeModified = datetime.datetime.today()
                     )
    newComment.save()
    
    alreadyNotified = []
    
    notifyPicOwner = Notification(
                                  targetUser = pic.targetId,
                                  picture = pic,
                                  type = "comment",
                                  text = "Someone has commented on " + pic.title,
                                  )
    notifyPicOwner.save()
    alreadyNotified.append(notifyPicOwner.targetUser.fbId)
    
    for i in Comment.objects.filter(picture=Picture.objects.get(id=data.get("picId",1))):
        if not i.poster.fbId in alreadyNotified:
            alreadyNotified.append(i.poster.fbId)
            newNotification = Notification(
                                           targetUser = User.objects.get(id = i.poster.id),
                                           picture = pic,
                                           type = "comment",
                                           text = "Someone has commented on " + pic.title,
                                           )
            newNotification.save()
            
    return HttpResponse("done")

@csrf_exempt
def vote(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    picId = data.get("picId", 1)
    userFbId = data.get("fbUserId",1)
    
    pic = Picture.objects.get(id = picId )
    
    if data.get("upVote", False):
        pic.upVote += 1
    elif data.get("downVote", False):
        pic.downVote +=1
    
    pic.save()
    
    if not Vote.objects.filter(user = User.objects.get(fbId= userFbId), picture = Picture.objects.get(id = picId)).exists():
        newVote = Vote(user = User.objects.get(fbId= userFbId ),
                       picture = Picture.objects.get(id= picId),
                       voteUp = data.get("voteUp", "null")
                       )
        newVote.save()

    return HttpResponse("Vote Added")
    

@csrf_exempt
def getFriends(request):
    
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    fbId = data.get("fbUserId", 1)
    
    user = User.objects.get(fbId= fbId)
    friends = Friend.objects.filter(parentFriend = user, friendId__activated = True) 
    
    j={}
    j['activeFriends'] = []
    
    for friend in friends:
        j['activeFriends'].append(friend.friendId.fbId)
    
    j['testingOnly'] = []
    for friend in friends:
        temp = {}
        temp['name'] = friend.friendId.fbName
        temp['fbId'] = friend.friendId.fbId
        j['testingOnly'].append(temp)
    
    return HttpResponse(json.dumps(j), content_type="application/json")

@csrf_exempt
def getNotification(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    fbId = data.get("fbUserId",1)
    
#     fbId=1
    j={}
    try:
        notification = Notification.objects.filter(targetUser=User.objects.get(fbId=fbId))[0]
        if notification.targetUser != None:
            j['targetFbName'] = notification.targetUser.fbName
        else:
            j['targetFbName'] = "none"
        if notification.poster != None:
            j['posterFbName'] = notification.poster.fbName
        else:
            j['posterFbName'] = "none"
        if notification.picture != None:
            j['picCaption'] = notification.picture.caption
            j['picId'] = notification.picture.id
            j['picTitle'] = notification.picture.title
        else:
            j['picCaption'] = "none"
            j['picId'] = "none"
            j['picTitle'] = "none"
        
        j['type'] = notification.type
        if notification.text != None:
            j['text'] = notification.text
        else:
            j['text'] = "none"
        
        notification.delete()
    except:
        return HttpResponse("none")
    return HttpResponse(json.dumps(j), content_type="application/json")

def signup(request):
    
    if "email" in request.GET:
        email = request.GET['email']
        print email

        to = "raytochina@gmail.com"
        gmail_user = 'soplogmedusa@gmail.com'
        gmail_pwd = 'supermanfly821'
        smtpserver = smtplib.SMTP("smtp.gmail.com",587)
        smtpserver.ehlo()
        smtpserver.starttls()
        smtpserver.ehlo
        smtpserver.login(gmail_user, gmail_pwd)
        header = 'To:' + to + '\n' + 'From: ' + gmail_user + '\n' + 'Subject: Signup for Alpha DerpTeam'  +'\n'
        header = header.encode("utf-8", 'ignore')
        msg = header + " Email: " + email
        msg = msg.encode("utf-8", "ignore")
        smtpserver.sendmail(gmail_user, to, msg)
    
        smtpserver.close()
    
        return HttpResponse("email request sent")
    
    return HttpResponse("no email selected")

'''
def emailUser(logBoolNotify):
    to = logBoolNotify.user.email
    gmail_user = 'soplogmedusa@gmail.com'
    gmail_pwd = 'supermanfly821'
    smtpserver = smtplib.SMTP("smtp.gmail.com",587)
    smtpserver.ehlo()
    smtpserver.starttls()
    smtpserver.ehlo
    smtpserver.login(gmail_user, gmail_pwd)
    header = 'To:' + to + '\n' + 'From: ' + gmail_user + '\n' + 'Subject: ' + logBoolNotify.logBool.step.name +" id:"+ str(logBoolNotify.id) +'\n'
    header = header.encode("ascii", 'ignore')
    msg = header + "\n\n" + logBoolNotify.logBool.step.name + " on: " + str(logBoolNotify.completeBy) + " : regarding - " +logBoolNotify.logBool.addText
    msg = msg.encode("ascii", "ignore")
    smtpserver.sendmail(gmail_user, to, msg)

    smtpserver.close()

'''

@csrf_exempt
def getStats(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
     
    fbId = data.get("fbUserId",1)

#     fbId = 655136010
    j={}
    j['postedPics'] =[]
    pics = Picture.objects.filter(posterId=User.objects.get(fbId=fbId)).order_by("-upVote")[:5]
    for p in pics:
        temp = {}
        temp['targetFbId'] = str(p.targetId.fbId)
        temp['picId'] = p.id
        temp['title'] = p.title
        temp['caption'] = p.caption
        temp['upVote'] = p.upVote
        temp['downVote'] = p.downVote
        temp['popularity'] = p.popularity
        temp['imageUrl'] = str(p.image)
        j['postedPics'].append(temp)
        
    return HttpResponse(json.dumps(j), content_type="application/json")

@csrf_exempt
def bugReport(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    newBugReport = BugReport(
                             user = User.objects.get(fbId = data.get("fbUserId", 1)),
                             bug = str(data.get("bugMessage",""))
                             )
    newBugReport.save()
    
    to = "raytochina@gmail.com"
    gmail_user = 'soplogmedusa@gmail.com'
    gmail_pwd = 'supermanfly821'
    smtpserver = smtplib.SMTP("smtp.gmail.com",587)
    smtpserver.ehlo()
    smtpserver.starttls()
    smtpserver.ehlo
    smtpserver.login(gmail_user, gmail_pwd)
    header = 'To:' + to + '\n' + 'From: ' + gmail_user + '\n' + 'Subject: BUG'  +'\n'
    header = header.encode("utf-8", 'ignore')
    msg = header + data.get("bugMessage","") 
    msg = msg.encode("utf-8", "ignore")
    smtpserver.sendmail(gmail_user, to, msg)
    
    return HttpResponse("Report submitted")

@csrf_exempt
def uploadError(request):
    f = open("error.html", "w")
    f.write(request.FILES['error'].read())
    f.close()
    return HttpResponse("")

def latestError(request):
    f = open( os.getcwd() + "/error.html", "rb")
    #f = open( "E:\\coding_workspace\\medusa_backend\\tempJson", "rb")
    stringReturn = f.read()
    return HttpResponse(stringReturn)
    
    
