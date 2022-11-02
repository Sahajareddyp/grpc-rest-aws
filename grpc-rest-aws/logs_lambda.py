import json
import boto3
from datetime import datetime

s3 = boto3.client('s3')


def binarySearch(logs,curr_time,delta,low=0,high=0): #using binary search to find the logs present
    high = len(logs) -1
    while(low<=high):
        mid = (high + low) // 2
        #  splitting the time with space, delim and index=0
        mid_time = datetime.strptime(logs[mid].split(" ")[0], "%H:%M:%S.%f")


        if (abs(curr_time - mid_time).total_seconds()/60) <= delta:
            return True
        elif mid_time <= curr_time :
            low = mid + 1
        else:
            high = mid - 1

    return False #not found

def lambda_handler(event, context):
    # Extract the query string parameter sent by the client

     time = event['queryStringParameters']['time']
     delta = event['queryStringParameters']['delta']

    # define s3 object
    get_s3_object =  boto3.resource('s3').Object("outputlogs-cs-441","input.log")

    # read the log file
    logs = get_s3_object.get()['Body'].read().decode('utf-8').splitlines()


    # convert the string to datetime
    curr_time = datetime.strptime(time, "%H:%M:%S.%f")

    if binarySearch(logs,curr_time,int(delta)) == False:
        return {
            'statusCode': 400,
            'body': json.dumps('No Logs found within provided time and delta')
        }
    else: # logs found
        return {
            'statusCode': 200,
            'body': json.dumps('Logs found within provided time and delta!')
        }