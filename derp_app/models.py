# -*- coding: utf-8 -*-
from django.db import models


# Create your models here.
class User(models.Model):
    name = models.CharField(max_length=50, null=True, blank=True)
    phone = models.CharField(max_length=20, null=True, blank=True)
    email = models.CharField(max_length=15, null=True, blank=True)
    
#    fbId = models.IntegerField(unique=True)
    fbId = models.BigIntegerField(unique=True)
    fbName = models.CharField(max_length=40)
    activated = models.BooleanField(default=False)
    
    def __unicode__(self):
        return str(self.id) + " Active: " + str(self.activated)#str(self.fbName) + "-" +str(self.id) + " Active:" + str(self.activated)



#        
class Friend(models.Model):
    parentFriend = models.ForeignKey("User",related_name='parentFriend')
    friendId = models.ForeignKey("User",related_name='friendId')
   
    def __unicode__(self):
        return str(self.parentFriend.id) + " - " + str(self.friendId.id)
        
        #return str(self.parentFriend.fbName) + " - " + str(self.friendId.fbName)
        

class Picture(models.Model):
    posterId = models.ForeignKey("User", related_name="poster")
    targetId = models.ForeignKey("User", related_name="targetUser")
    popularity = models.IntegerField(null=True, blank=True)
    upVote = models.IntegerField(default=0, blank=True)
    downvote = models.IntegerField(default=0, blank=True)
    views = models.IntegerField(default=0,blank=True)
    image = models.FileField(upload_to="/derp_media/%Y/%m/%d")
    caption = models.CharField(max_length=50, blank=True, null=True)
    
    def __unicode__(self):
        return str(self.id)
    
    
class Comment(models.Model):
    picture = models.ForeignKey("Picture")
    poster = models.ForeignKey("User")
    comment = models.TextField(blank=True, null=True)
    timeModified = models.DateTimeField(blank=True,null=True)