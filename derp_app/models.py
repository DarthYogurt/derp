from django.db import models

# Create your models here.
class User(models.Model):
    name = models.CharField(max_length=50, null=True, blank=True)
    phone = models.CharField(max_length=20, null=True, blank=True)
    email = models.CharField(max_length=15, null=True, blank=True)
    
    fbId = models.IntegerField()
    fbName = models.CharField(max_length=40)
    
    def __unicode__(self):
        return str(self.name) + "-" +str(self.id)
    
class Friend(models.Model):
    parentFriend = models.ForeignKey("User")
    name = models.CharField(max_length=50, null=True, blank=True)
    fbId = models.IntegerField()
    dateAdded = models.DateField(null=True, blank=True)
    
    
    def __unicode__(self):
        return str(self.id) + "-" + str(self.name)