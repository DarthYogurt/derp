from django.db import models

# Create your models here.
class User(models.Model):
    name = models.CharField(max_length=50, null=True, blank=True)
    phone = models.CharField(max_length=20, null=True, blank=True)
    email = models.CharField(max_length=15, null=True, blank=True)
    
    fbId = models.ForeignKey("FacebookDb", null=True,blank=True)
    fbName = models.CharField(max_length=40)
    
    def __unicode__(self):
        return str(self.name) + "-" +str(self.id)

class FacebookDb(models.Model):
    name = models.CharField(max_length=50, null=True, blank=True)
    fbId = models.IntegerField()
    dateAdded = models.DateField(null=True, blank=True) 
       
class Friend(models.Model):
    parentFriend = models.ForeignKey("User",related_name='Friend_parentId')
    friendId = models.ForeignKey("User",related_name='Friend_friendId')
    fbId = models.ForeignKey("FacebookDb")
#     name = models.CharField(max_length=50, null=True, blank=True)
#     fbId = models.IntegerField()
    dateAdded = models.DateField(null=True, blank=True)
    
    def __unicode__(self):
        return str(self.id) + "-" + str(self.name)
    

class Pic(models.Model):
    poster = models.ForeignKey("User")
    popularity = models.IntegerField(null=True, blank=True)
    upVote = models.IntegerField(null=True, blank=True)
    downvote = models.IntegerField(null=True, blank=True)
    views = models.IntegerField(null=True,blank=True)
    #image = models.ImageField(null=True, blank=True)
    targetFbId = models.IntegerField()