#include <iostream>
#include <bits/stdc++.h>
#include <fstream>
using namespace std;

struct Tuple{
  char arr[16];
};

Tuple* newnode(char arx[])
{
    struct Tuple* tuple = (Tuple*)malloc(sizeof(Tuple));
    for(int i=0;i<16;i++)
    {
        tuple->arr[i]=arx[i];
    }
    return tuple;
}


int data[101][16];
int result[101];


char specf[] = {'#','#','#','#','#','#','#','#','#','#','#','#','#','#','#','#'};
Tuple* Specific_bounday = newnode(specf);
char genex[]={'?','?','?','?','?','?','?','?','?','?','?','?','?','?','?','?'};
Tuple *tt = newnode(genex);
vector<Tuple* > General_boundary;





void update_specific(Tuple* instance){
  
    for(int i=0;i<16;i++)
    {
        if(Specific_bounday->arr[i]=='?'||instance->arr[i]==Specific_bounday->arr[i])
        {
            continue;
        }
        else if(Specific_bounday->arr[i]=='#')
        {
            Specific_bounday->arr[i] = instance->arr[i];
        }
        else
        {
           Specific_bounday->arr[i] = '?';
        }
    }
}


void change_legs(int i,Tuple* temp,Tuple* instance)
{
    char legs[6] = {'0','2','4','5','6','8'} ;
    Tuple* vec = instance;  
    for(int j = 0;j<6;j++)
    {
        if(legs[j]==instance->arr[i])
            continue;
        else
        {
            vec->arr[i]=legs[j];
            General_boundary.push_back(vec);
        }
    } 
}

void compare_general_with_specific()
{
    vector<int> del;
    for(int i=0;i<General_boundary.size();i++)
    {
        Tuple* temp = General_boundary[i];
        for(int j=0;j<16;j++)
        {
            if(temp->arr[i]=='?'||temp->arr[i]==Specific_bounday->arr[i])
                continue;
            else {
                del.push_back(i);
                break;
            }

        }
    }
}


void bool_Change(int i,Tuple* temp)
{
    if(temp->arr[i]=='0')
    {
        temp->arr[i]='1';
    }
    else
    {
        temp->arr[i]=='0';   
    }
    General_boundary.push_back(temp);
}


void update_general(Tuple* instance)
{
    int j =0;
    vector<int> del;
    vector<Tuple* > newgeneral;
    int l_gen = General_boundary.size();
        for(int j=0;j<l_gen;j++)
        {
            Tuple* temp = General_boundary[j];
            for(int i=0;i<16;i++)
            {
                if(temp->arr[i]=='?')
                {
                    if(i!=12)
                    {
                        bool_Change(i,temp);
                        del.push_back(j);
                    }
                    else
                    {
                        //leg_Change(i,temp);
                        //changing the lef attribute

                    }  
                }
                else
                {
                    if(temp->arr[i]!=instance->arr[i])
                        continue;
                    else
                    {
                        del.push_back(j);
                        break;
                    }
                }
            }
        }
    
}

// void readfile()
// {
//     char ch;
//     ifstream zoo ("data.txt");
//     if (zoo.is_open())
//     {   
//         while(getchar())
//         {

//         }
//     }
// }

int main() {
	return  0;
}
