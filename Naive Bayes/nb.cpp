#include <bits/stdc++.h>
#include <fstream>

using namespace std;
int total_words; // total no.of distinct words in training data
int word_freq[89527][2]; // frequency of words in positive and negative documents
int correct; // no of documents correctly predicted
int count_pos, count_neg; // no of words in positive, negative documents
int tp, fp, fn, tn;
map <string, int> word_index;// map which maps words with their respective integers

void create_word_freq(string filename) // function to read and store the word-frequencies in a 2d matrix
{

    ifstream infile(filename);
    string line;
    while(getline(infile, line))
    {
        istringstream iss(line);
        string rating_str;
        iss >> rating_str;
        int rating;
        stringstream geek(rating_str);
        geek >> rating;

        string word;
        while(iss >> word)
        {
            int word_id, freq;
            char colon;
            istringstream new_stream(word);
            new_stream >> word_id >> colon >> freq;

            if(rating > 5)
            {
                word_freq[word_id][0] += freq;
                count_pos += freq;
            }
            else
            {
                word_freq[word_id][1] += freq;
                count_neg += freq;
            }
        }
    }
}

double calc_prob(int word_id, int ps) // function to calculate probability for given word
{

    float num = word_freq[word_id][ps] + 1;
    float den;
    if(ps == 0)
        den = count_pos + total_words;
    else
        den = count_neg + total_words;
    float log_prob = log(num/den);

    return log_prob;
}
float pred_acc(string filename) // function to calculate probabilities of all documents
{
    ifstream infile(filename);
    string line;
    while(getline(infile, line))
    {
        istringstream iss(line);
        string rating_str;
        iss >> rating_str;
        int target;
        int rating;
        stringstream geek(rating_str);

        geek >> rating;

        if(rating > 5)
            target = 0;
        else
            target = 1;
        string word;

        double pos_prob=0, neg_prob=0;

        while(iss >> word)
        {
            int word_id, freq;
            char colon;
            istringstream new_stream(word);
            new_stream >> word_id >> colon >> freq;

            pos_prob += freq*calc_prob(word_id, 0);
            neg_prob += freq*calc_prob(word_id, 1);
        }
        int pred;

        if(pos_prob > neg_prob)
            pred = 0;
        else
            pred = 1;

        if(pred == target)
            correct++;

	if(pred == 0 && target == 0)
            tp++;
        if(pred == 0 && target == 1)
            fp++;
        if(pred == 1 && target == 0)
            fn++;
        if(pred == 1 && target == 1)
            tn++;

    }
    float acc = (float)correct/25000;
    return acc;
}

void create_index(string filename) // create an index that maps words to their corresponding numbers
{
    ifstream infile(filename);
    string line;
    int i = 0;
    while(getline(infile, line))
    {
        word_index.insert(make_pair(line, i++));
    }

}

void remove_stop_words(string filename) // function to remove stop words from training and testing data
{

    ifstream infile(filename);
    string line;
    int count_words = 0;
    while(getline(infile, line))
    {
        map<string, int> :: iterator it;
        it = word_index.find(line);
        int word_id = it->second;
        if(it != word_index.end())
        {

            count_words++;
        count_pos -= word_freq[word_id][0];
        count_neg -= word_freq[word_id][1];

        word_freq[word_id][0] = 0;
        word_freq[word_id][1] = 0;
        }
    }
    total_words -= count_words;
	total_words -= count_words;
    tp = 0;
    fp = 0;
    tn = 0;
    fn = 0;
}

int main()
{
    total_words = 89527;
    create_word_freq("train.txt");
    float accuracy = pred_acc("labeledBow.feat");
    cout << "Accuracy without removal of stop-words: "<< accuracy << endl;

	float pos_prec, pos_rec, pos_f1, neg_prec, neg_rec, neg_f1;
    pos_prec = (float)tp/(tp+fp);
    pos_rec = (float)tp/(tp+fn);
    pos_f1 = 2*(pos_prec*pos_rec)/(pos_prec+pos_rec);
    cout << "Positive Precision: " << pos_prec << endl;
    cout << "Positive Recall: " << pos_rec << endl;
    cout << "Positive F1: " << pos_f1 << endl;

    neg_prec = (float)tn/(tn+fn);
    neg_rec = (float)tn/(tn+fp);
    neg_f1 = 2*(neg_prec*neg_rec)/(neg_prec+neg_rec);
    cout << "Negative Precision: " << neg_prec << endl;
    cout << "Negative Recall: " << neg_rec << endl;
    cout << "Negative F1: " << neg_f1 << endl << endl;

    create_index("imdb.vocab");
    remove_stop_words("stop-word-list.txt");
    correct = 0;
    cout <<"Accuracy after removal of stop-words: "<< pred_acc("test_labeledBow.feat")<<endl;

    pos_prec = (float)tp/(tp+fp);
    pos_rec = (float)tp/(tp+fn);
    pos_f1 = 2*(pos_prec*pos_rec)/(pos_prec+pos_rec);
    cout << "Positive Precision: " << pos_prec << endl;
    cout << "Positive Recall: " << pos_rec << endl;
    cout << "Positive F1: " << pos_f1 << endl;

    neg_prec = (float)tn/(tn+fn);
    neg_rec = (float)tn/(tn+fp);
    neg_f1 = 2*(neg_prec*neg_rec)/(neg_prec+neg_rec);
    cout << "Negative Precision: " << neg_prec << endl;
    cout << "Negative Recall: " << neg_rec << endl;
    cout << "Negative F1: " << neg_f1 << endl;

    return 0;
}
