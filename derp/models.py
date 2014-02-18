from django.db import models


class User(models.Model):
    
    ''' currently not connected to Group 
    '''
    name = models.CharField(max_length=30)
    phone = models.CharField(max_length=15)
    email = models.CharField(max_length=30)
    
    fbId = models.IntegerField()
    fbName = models.CharField()
    
    
    def __unicode__(self):
        return self.name + "-"+ str(self.id)

class FriendList(models.Model):
    
    user = models.ForeignKey("User")
    fbId = models.IntegerField()
    dateAdded = models.DateTimeField()
    account = models.ForeignKey("User", null=True, blank=True)
    
    def __unicode__(self):