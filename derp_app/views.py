# -*- coding: utf-8 -*-
import datetime
import json
import os
import random
import sys

from django.core.paginator import Paginator, PageNotAnInteger, EmptyPage
from django.http.response import HttpResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt

from derp_app.models import *


# Create your views here.
@csrf_exempt
def login(request):
    
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    #for d in data:  print d,data[d]
    
    if User.objects.filter(fbId=data.get('fbUserId',0)).exists():
        #print "User exists"
        True
        # need to update user info
    else:
        #print "not exist"
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
                           activated = False
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
    
    for d in data:
        print d,data[d]
    
    print request.FILES['image']
    

    newPicture = Picture(
                         posterId = User.objects.get(fbId=data.get("fbUserId",1)),
                         targetId = User.objects.get(fbId=data.get("targetFbId", 1)),
                         image = request.FILES['image'],
                         caption = data.get("caption", 0),
                         title = data.get("title",""),
                         date = datetime.datetime.now()
                         )
    newPicture.save()
    return HttpResponse("")

def getUserId(request,fbUserId):
    try:
        return HttpResponse(User.objects.get(fbId=fbUserId).id)
    except:
        return HttpResponse("User does not exist")
    

def getPic(request, picId):
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
    j['posterUserId'] = str(pic.posterId.id)
    j['posterFbId'] = str(pic.posterId.fbId)
    j['imageUrl'] = request.get_host() + str(pic.image)
    j['caption'] = pic.caption
    j['views'] = pic.views
    j['title'] = pic.title
    j['popularity'] = str(pic.popularity)
    
    j['comments'] = []
    
    for comment in Comment.objects.filter(picture=Picture.objects.get(id=picId)):
        com = {}
        com['comPoster'] = comment.poster.id
        com['comText'] = comment.comment
        com['comTime'] = comment.timeModified
    return HttpResponse(json.dumps(j), content_type="application/json")

def getTeamGallery(request,userId):
    user = User.objects.get(id=userId)
    pictures = Picture.objects.filter(targetId = user)    
    j={}
    j['teamId'] = userId
    j['teamGallery'] =[]
    for p in pictures:
        temp = {}
        #temp['teamId'] = p.posterId.id
        temp['posterId'] = p.posterId.id
        temp['popularity'] = p.popularity
        temp['upVote'] = p.upVote
        temp['downVote'] = p.downVote
        temp['views'] = p.views
        temp['image'] = str(request.get_host()) + str(p.image)
        temp['caption'] = p.caption
        temp['title'] = p.title
        j['teamGallery'].append(temp)
    return HttpResponse(json.dumps(j), content_type="application/json")

def gallery(request, numPerPage, pageNum):
     
    picture_list = Picture.objects.order_by("date")
    paginator = Paginator(picture_list, numPerPage)
    page = pageNum
     
    try:
        pictures = paginator.page(page)
    except PageNotAnInteger:
        pictures = paginator.page(1)
    except EmptyPage:
        pictures = paginator.page(paginator.num_pages)
    
    j={}
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
    
    for d in data:
        print d,data[d]

    newComment = Comment(
                         picture = Picture.objects.get(id = data.get("pictureId", 1)),
                         poster = User.objects.get(id = data.get("posterId",1)),
                         comment = data.get("comment",""),
                         timeModified = datetime.datetime.today()
                         )

    return HttpResponse("done")



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
    