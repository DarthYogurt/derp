import json
import os
import sys

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
                       fbName = data.get("fbUserName", ""),
                       activated = True,                   
                       )
        newUser.save()

    
    for friend in data['fbFriends'][:11]:
        fbName =""
        try:
            fbName = friend['fbName'] #.encode("utf-8") #.encode("utf-8")   
        except:
            fbName = "Not Available Charset"    
        if User.objects.filter(fbId=friend.get('fbId',0)).exists():
            #print "already in User Database"
            #Update this friend again
            updateUser = User.objects.get(fbId=friend.get('fbId',0))
            updateUser.fbName = fbName #friend.get("fbName",0)
            updateUser.save()
        else:
            newUser = User(
                           fbId=friend.get("fbId",0),
                           fbName= fbName, #friend.get("fbName",0),
                           activated = False
                           )
            newUser.save()
             
        if Friend.objects.filter(parentFriend = User.objects.get(fbId =data.get('fbUserId',0)), 
                                 friendId = User.objects.get(fbId = friend.get("fbId",0))
                                 ).exists():
            True #already friends can just do an update
        else:
            newFriend = Friend(
                               parentFriend = User.objects.get(fbId=data.get("fbUserId",0)),
                               friendId = User.objects.get(fbId=friend.get("fbId",0))
                               )
            newFriend.save()
     
    return HttpResponse("")



@csrf_exempt
def uploadPic(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    #for d in data:
    #    print d,data[d]
    
    print request.FILES['image']
    
    
    newPicture = Picture(
                         poster = User.objects.get(fbId=data.get("fbUserId",0)),
                         targetFbId = User.objects.get(fbId=data.get("targetFbId",0)),
                         #image = request.FILES['image'],
                         )
    newPicture.save()
    return HttpResponse("")


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
    